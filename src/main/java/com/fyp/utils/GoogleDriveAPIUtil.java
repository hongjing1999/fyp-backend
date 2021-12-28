package com.fyp.utils;

import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class GoogleDriveAPIUtil {
	
//	private String getAccessToken() {
//		
//	}
	private static String privateKey = System.getenv("GOOGLE_KEY");
			
	
	private static  String getAccessToken() {
		String jwt = generateJWT();
		String url = "https://oauth2.googleapis.com/token?grant_type=urn:ietf:params:oauth:grant-type:jwt-bearer&assertion="+jwt;
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<TokenResponse> result = restTemplate.postForEntity(url, null, TokenResponse.class);

		return result.getBody().getAccess_token();
	}
	
	private static String generateJWT() {
		String issuer = "backend-server@fyp-hj.iam.gserviceaccount.com";
		
		String audience = "https://oauth2.googleapis.com/token";
		
		String scope = "https://www.googleapis.com/auth/drive";

        String retStr = null;


        // strip the headers
        privateKey = privateKey.replace("-----BEGIN PRIVATE KEY-----", "");
        privateKey = privateKey.replace("-----END PRIVATE KEY-----", "");
        privateKey = privateKey.replaceAll("\\s+","");
        

        try {

        	SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.RS256;
            Instant nowInstant = Instant.now();
            Instant expInstant = nowInstant.plus(Duration.ofHours(1));
            Date now = Date.from(nowInstant);
            Date exp = Date.from(expInstant);   

            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKey));
            KeyFactory kf = KeyFactory.getInstance("RSA");
            PrivateKey privKey = kf.generatePrivate(keySpec);
            
            JwtBuilder builder = Jwts.builder()
                                        .setIssuedAt(now)
                                        .setIssuer(issuer)
                                        .setAudience(audience)
                                        .claim("scope",scope)
                                        .signWith(privKey)
                                        .setExpiration(exp);         
            return builder.compact();

        } catch (Exception e) {
            e.printStackTrace();
        } 
        
        return retStr;
    }
	
	public static void printPrivateKey() {
		System.out.println("--------------------------"+ privateKey);
	}
	
	public static String uploadFile(String contentType, String filename, String parent, String fileContent) {
		ObjectMapper mapper = new ObjectMapper();
		String accessToken = "Bearer " + getAccessToken();
		String url = "https://www.googleapis.com/upload/drive/v3/files?uploadType=multipart";
    	RestTemplate restTemplate = new RestTemplate();
    	
    	HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.add("Authorization", accessToken);
        

        Metadata metadata = new Metadata();
        metadata.setMimeType(contentType);
        metadata.setName(filename);
        
        ArrayList<String> parents = new ArrayList<String>();
        parents.add("14bW_E-YB2IM5jCg3aYC5PqyeaEuJfWWC");
        parents.add(parent);
        String[] parentsArray  = new String[parents.size()];
        parentsArray = parents.toArray(parentsArray);
        metadata.setParents(parentsArray);
        

        String json = "";
        try {
        	json = mapper.writeValueAsString(metadata);
        } catch (JsonProcessingException e) {
        	e.printStackTrace();
        }


        
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("metadata", json).header("Content-Type", "application/json");
        if(fileContent != null) {
        	builder.part("media", fileContent).header("Content-Type", contentType);
        }
        
        MultiValueMap<String, HttpEntity<?>> parts = builder.build();
        
        HttpEntity<MultiValueMap<String, HttpEntity<?>>> request = new HttpEntity<MultiValueMap<String, HttpEntity<?>>>(parts, headers);
        ResponseEntity<GoogleDriveFile> result = restTemplate.postForEntity(url, request, GoogleDriveFile.class);
        return result.getBody().getId();
        
	}
	
	public static String searchFile(String fileName, boolean isFolder) {
		ObjectMapper mapper = new ObjectMapper();
		String accessToken = "Bearer " + getAccessToken();
		System.out.print("TOKEN-----------------------------------"+accessToken);
		String url = "https://www.googleapis.com/drive/v3/files";
		
		String queryString = "'14bW_E-YB2IM5jCg3aYC5PqyeaEuJfWWC' in parents and trashed = false and name = '" + fileName + "'";
		if(isFolder) {
			queryString = "mimeType = 'application/vnd.google-apps.folder' and " + queryString;
		}
		url = url +"?q=" + queryString;
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", accessToken);
		
		
		HttpEntity<?> request = new HttpEntity<>(headers);

		ResponseEntity<GoogleDriveFileList> result = restTemplate.exchange(url, HttpMethod.GET, request, GoogleDriveFileList.class);
		List<GoogleDriveFile> fileList =  result.getBody().getFiles();
		if(fileList.size()>0) {
			return fileList.get(0).id;
		}
		else {
			return null;
		}
		
	}
	
	
	private static class Metadata{
		String name;
		String mimeType;
		String[] parents;
		
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getMimeType() {
			return mimeType;
		}
		public void setMimeType(String mimeType) {
			this.mimeType = mimeType;
		}
		public String[] getParents() {
			return parents;
		}
		public void setParents(String[] parents) {
			this.parents = parents;
		}
		
	}
	
	private static class TokenResponse {
		String access_token;
		String token_type;
		int expires_in;
		
		public String getAccess_token() {
			return access_token;
		}
		public void setAccess_token(String access_token) {
			this.access_token = access_token;
		}
		public String getToken_type() {
			return token_type;
		}
		public void setToken_type(String token_type) {
			this.token_type = token_type;
		}
		public int getExpires_in() {
			return expires_in;
		}
		public void setExpires_in(int expires_in) {
			this.expires_in = expires_in;
		}
		
	}
	
	private static class GoogleDriveFileList{
		List<GoogleDriveFile> files;

		public List<GoogleDriveFile> getFiles() {
			return files;
		}

		public void setFiles(List<GoogleDriveFile> files) {
			this.files = files;
		}
		
	}
	
	private static class GoogleDriveFile {
		String id;
		String name;
		String mimeType;
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getMimeType() {
			return mimeType;
		}
		public void setMimeType(String mimeType) {
			this.mimeType = mimeType;
		}
		
		
		
	}
}
