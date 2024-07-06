# Web/Was 구현

### 1. HTTP/1.1 의 Host 헤더를 해석하세요.
#### 예를 들어, a.com 과 b.com 의 IP 가 같을지라도 설정에 따라 서버에서 다른 데이터를 제공할 수 있어야 합니다.
#### 아파치 웹 서버의 VirtualHost 기능을 참고하세요.

` HTTP 요청의 Host 헤더에 따라 다른 서버 설정을 적용할 수 있도록 구현 완료`

### 2. 다음 사항을 설정 파일로 관리하세요.
#### 파일 포맷은 JSON 으로 자유롭게 구성하세요.
` typesafe를 이용한 application.json에서 설정파일 관리 합니다. 8090 포트 사용`

### 403, 404, 500 오류를 처리합니다.
#### 해당 오류 발생 시 적절한 HTML 을 반환합니다.
` Exception을 활용한 오류 페이지별 처리 `

### 4. 다음과 같은 보안 규칙을 둡니다.
#### 다음 규칙에 걸리면 응답 코드 403 을 반환합니다.
#### HTTP_ROOT 디렉터리의 상위 디렉터리에 접근할 때, 확장자가 .exe 인 파일을 요청받았을 때
` blocked_extension 설정을 통하여 보안 규칙 적용 `

### 5. logback 프레임워크 http://logback.qos.ch/를 이용하여 다음의 로깅 작업을 합니다.
` 일별 log 로깅 관리 설정`

### 6. 간단한 WAS 를 구현합니다.
``` 
1. package 별 라우터 설정할수 있는 구조 설계

2. config load시 라우터에 등록

3. URL 을 SimpleServlet 구현체로 매핑
> http://localhost:8090/Hello /
> http://localhost:8090/service.Hello /
> http://localhost:8090/Hello?name=sb /
> http://localhost:8090/service.Hello?name=sb
```

### 7. 현재 시각을 출력하는 SimpleServlet 구현체를 작성하세요.
` http://localhost:8090/date `

### 8. JUnit4를 이용한 테스트 케이스 작성
` HttpServerTest 구현 서버 실행 및 servlet 작동 여부 테스트 `







