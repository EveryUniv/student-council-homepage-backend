**Naming Convention은 반드시 준수** (Bold 처리), 그 외에는 권장 사항

## Java
 - **일반 Java Naming Convention을 따름**
 - 패키지 이름: 모두 소문자. 특수문자 (언더바 포함) 금지
 - 클래스 이름: Camel case
 - 등... 틀리면 IntelliJ가 Weak warning을 띄워준다.

## DB
 - **Table 이름**: 대문자 및 언더바(_)

## Entity
 - **Primary Key (ID) 이름**: [Entity명 소문자]_id
   - Category Entity -> category_id
 - 생성자 Builder 패턴 사용

## Test
 - 가능한 Unit 테스트로 구성
 - 가능한 given / when / then으로 구성
 - Controller 테스트는 @WebMvcTest 사용
 - Service 테스트는 Mockito 사용
 - Repository 테스트는 @DataJpaTest 사용
 - @Value 는 생성자 주입 활용