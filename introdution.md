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

## Git

### Type
 - **feat**: 기능 추가
 - **fix**: 버그 수정
 - **hotfix**: 다음 Release전에 급히 수정되어야 할 버그 수정
 - **refactor**: 코드 리팩토링
 - **test**: 테스트 추가 및 수정 (기능 추가와 함께 올라온 test는 feat으로 간주)
 - **docs**: 문서 추가 및 수정
 - **chore**: 프로젝트 설정 변경 (gradle, lombok, app 등...)
### Commit / Branch Naming Convention
 - [커밋 타입]: [커밋 메시지]