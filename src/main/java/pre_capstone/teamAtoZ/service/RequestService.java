package pre_capstone.teamAtoZ.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class RequestService {
    private static final Integer TIME_OUT = 5000;
    @Value("${ppurio.api.key}")
    private String API_KEY = "fd598f5d9d4f03e573a9a3774e3c8889c344fc2d5343f67c9c73c7530206059d";
    @Value("${ppurio.account}")
    private String PPURIO_ACCOUNT = "hansunga";
    @Value("${ppurio.from}")
    private String FROM;
    @Value("${ppurio.file.path}")
    private String FILE_PATH;
    @Value("${ppurio.uri}")
    private String URI = "https://message.ppurio.com";


        public Map<String, Object> requestSend(String title, String content,String filePath, List<String> phoneNumber) throws FileNotFoundException {
            FILE_PATH = filePath;

            // 파일 존재 여부 확인 코드 추가
            File file = new File(FILE_PATH);
            if (!file.exists() || !file.canRead()) {
                System.out.println("파일을 찾을 수 없거나 읽을 수 없습니다.");
                throw new FileNotFoundException(Map.of("status", "error", "message", "파일을 찾을 수 없거나 읽을 수 없습니다.").toString());
            }

            // 토큰 발급 및 발송 요청
            String basicAuthorization = Base64.getEncoder().encodeToString((PPURIO_ACCOUNT + ":" + API_KEY).getBytes());
            Map<String, Object> tokenResponse = getToken(basicAuthorization);
            Map<String, Object> sendResponse = send(URI, tokenResponse.get("token").toString(), FILE_PATH, title, content, phoneNumber);

            System.out.println("발송 응답: " + sendResponse);
            return sendResponse;
        }

        public void requestCancel() {
            String basicAuthorization = Base64.getEncoder().encodeToString((PPURIO_ACCOUNT + ":" + API_KEY).getBytes());
            Map<String, Object> tokenResponse = getToken(basicAuthorization);
            Map<String, Object> cancelResponse = cancel(URI, tokenResponse.get("token").toString());

            System.out.println(cancelResponse.toString());
        }

        private Map<String, Object> getToken(String basicAuthorization) {
            HttpURLConnection conn = null;
            try {
                Request request = new Request(URI + "/v1/token", "Basic " + basicAuthorization);
                conn = createConnection(request);
                return getResponseBody(conn);
            } catch (IOException e) {
                throw new RuntimeException("API 요청과 응답 실패", e);
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }

        // 파일 경로를 매개변수로 받아 `createSendTestParams()`에 전달하도록 수정
        public Map<String, Object> send(String baseUri, String accessToken, String filePath, String title, String content, List<String> phoneNumber) {
            HttpURLConnection conn = null;
            try {
                String bearerAuthorization = "Bearer " + accessToken;
                Request request = new Request(baseUri + "/v1/message", bearerAuthorization);

                // 파일 경로를 전달하여 실제 파일을 포함한 파라미터 생성
                conn = createConnection(request, createSendTestParams(filePath, title, content, phoneNumber));

                Map<String, Object> response = getResponseBody(conn);
                response.put("result", "성공");
                return response;
            } catch (IOException e) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("result", "실패");
                errorResponse.put("error", e.getMessage());
                return errorResponse;
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }

        private Map<String, Object> cancel(String baseUri, String accessToken) {
            HttpURLConnection conn = null;
            try {
                String bearerAuthorization = "Bearer " + accessToken;
                Request request = new Request(baseUri + "/v1/cancel", bearerAuthorization);
                conn = createConnection(request, createCancelTestParams());

                return getResponseBody(conn);
            } catch (IOException e) {
                throw new RuntimeException("API 요청과 응답 실패", e);
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }

        private <T> HttpURLConnection createConnection(Request request, T requestObject) throws IOException {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonInputString = objectMapper.writeValueAsString(requestObject);

            HttpURLConnection connect = createConnection(request);
            connect.setDoOutput(true);

            try (OutputStream os = connect.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return connect;
        }

        private HttpURLConnection createConnection(Request request) throws IOException {
            URL url = new URL(request.getRequestUri());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", request.getAuthorization());
            conn.setConnectTimeout(TIME_OUT);
            conn.setReadTimeout(TIME_OUT);
            return conn;
        }

        private Map<String, Object> getResponseBody(HttpURLConnection conn) {
            InputStream inputStream;
            try {
                inputStream = conn.getResponseCode() == 200 ? conn.getInputStream() : conn.getErrorStream();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                StringBuilder responseBody = new StringBuilder();
                String inputLine;
                while ((inputLine = br.readLine()) != null) {
                    responseBody.append(inputLine);
                }
                return convertJsonToMap(responseBody.toString());
            } catch (IOException e) {
                throw new RuntimeException("API 응답을 읽는 데 실패했습니다.", e);
            }
        }

        private Map<String, Object> convertJsonToMap(String jsonString) throws JsonProcessingException {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(jsonString, new TypeReference<>() {});
        }

        // `createSendTestParams` 메서드가 `filePath`를 매개변수로 받도록 수정
        private Map<String, Object> createSendTestParams(String filePath, String title, String content, List<String> receiver) throws IOException {
            HashMap<String, Object> params = new HashMap<>();

            //content를 100바이트 단위로 분리
            byte[] contentBytes = content.getBytes(StandardCharsets.UTF_8);

            if (contentBytes.length > 1000) {
                throw new RuntimeException("2000자 이하로 입력해주세요.");
            }

            //수신자 여러명 설정
            List<Map<String, Object>> targets = new ArrayList<>();
            for (String phoneNumber : receiver) {
                Map<String, Object> target = new HashMap<>();
                target.put("to", phoneNumber);
                target.put("name" , "");
                target.put("changeWord", Map.of("var1","test"));
                targets.add(target);
            }



            params.put("account", PPURIO_ACCOUNT);
            params.put("messageType", "MMS");
            params.put("from", FROM);
            params.put("content", content);
            params.put("duplicateFlag", "N");
            params.put("rejectType", "AD");
            params.put("targetCount", receiver.size());
            params.put("targets", targets);
            params.put("refKey", RandomStringUtils.random(32, true, true));
            params.put("subject", title);

            // `createFileTestParams` 호출 시 `filePath` 전달
            params.put("files", List.of(createFileTestParams(filePath)));
            return params;
        }

        // `createFileTestParams`가 `filePath`를 매개변수로 받도록 수정
        private Map<String, Object> createFileTestParams(String filePath) throws IOException {
            File file = new File(filePath);
            byte[] fileBytes = new byte[(int) file.length()];
            try (FileInputStream fileInputStream = new FileInputStream(file)) {
                if (fileInputStream.read(fileBytes) != file.length()) {
                    throw new IOException();
                }
                String encodedFileData = Base64.getEncoder().encodeToString(fileBytes);
                HashMap<String, Object> params = new HashMap<>();
                params.put("size", file.length());
                params.put("name", file.getName());
                params.put("data", encodedFileData);
                return params;
            } catch (IOException e) {
                throw new RuntimeException("파일을 가져오는데 실패했습니다.", e);
            }
        }

        private Map<String, Object> createCancelTestParams() {
            HashMap<String, Object> params = new HashMap<>();
            params.put("account", PPURIO_ACCOUNT);
            params.put("messageKey", "221128133505801SMS010542suchUL8P");
            params.put("messageType", "MMS");
            return params;
        }
    }

    @Getter
    class Request {
        private final String requestUri;
        private final String authorization;

        public Request(String requestUri, String authorization) {
            this.requestUri = requestUri;
            this.authorization = authorization;
        }
    }