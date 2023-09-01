## 개요
- **Naming Convention 준수** (Bold 처리), 그 외에는 권장 사항으로 반드시 지키지 않아도 된다.

## Java
 - **일반 Java Naming Convention을 따름**
   - 패키지 이름: `모두 소문자. 특수문자 (언더바 포함) 금지`
   - 클래스 이름: `Camel case`
   - 등... 틀리면 IntelliJ가 Weak warning을 띄워준다.
 - **`@Setter`와 `@Data`는 사용 금지.** setter는 필요한 부분만 직접 생성 권장
 - `@AllArgsConstructor`는 필드 순서가 뒤바뀌면 알아차리기 힘든 버그가 발생할 수 있으므로 직접 constructor를 생성하는 방식을 권장.
 - 필드에 final을 붙일 수 있으면 붙이기. 불변성 유지, 생성자 직접 생성시 누락된 필드 초기화를 발견할 수 있다.

## DB
 - **Table 이름**: `소문자 및 언더바(_)`
 - **Column 이름**: `소문자 및 언더바(_)`
 - 특별한 설정을 하지 않는 한 기본 규칙에 의해 자동으로 `소문자 및 언더바`로 변환된다.

## Entity
 - **Primary Key (ID) 이름**: `[Entity명 소문자]_id`
   - Category Entity -> category_id
   - Entity명이 너무 긴 경우(또는 2개 단어 이상)에는 줄여서 사용 가능
 - 인자가 모두 필수이고, 4개 미만인 경우에는 생성자 사용.
 - 그 외에는 Builder 패턴 사용. (필수 인자 처리가 필요하면 생성자 parameter에 `@lombok.NonNull`)
 - Builder 패턴은 생성자에 붙여 사용하고, 그 생성자는 반드시 private 처리
 - Lombok으로 생성자 만들 경우에는 access를 PROTECTED로 설정 권장

## Dto
 - **클래스 이름**: `[DomainName]``Dto`
 - 생성자와 `@Getter` 조합 사용
 - 인자가 너무 많으면(4개 이상) Builder로 전환
 - Response dto에서는 `@AllArgsConstructor` 사용을 지양하자. (필요한 경우에는 사용)
   - Field 순서가 뒤바뀌었을 때 dto가 가장 버그를 발견하기 어렵다.
   - 그래도 생성자가 필요하다면 가능한 불변성을 보장하는 `@RequiredArgsConstructor` 사용을 고려해보자.
   - Lombok으로 생성자 만들 경우에는 access를 PROTECTED로 설정 권장
 - Request Dto에서는 개발자가 아니라 spring이 만들어서 자동 주입해주므로 사용해도 문제없다.

## Test
 - 필수
   - **Service, Controller의 단위 테스트는 반드시 작성**
   - Repository는 사용자 정의 메서드(JPQL, QueryDSL, ...)에 대해 테스트 작성
 - 권장 사항
   - given / when / then으로 구성 (상황에 따라 생략, 추가 등 변형 가능)
   - MVC Service 테스트는 Mockito 사용.
   - MVC Controller 테스트는 `@WebMvcTest` 사용.
   - MVC Repository 테스트는 `@DataJpaTest` 사용.
     - Redis Repository는 AbstractContainerRedisTest를 상속받고 Docker를 띄워서 테스트.
     - 개발 환경에서만 테스트할 수 있게 테스트 클래스에 `@FullIntegrationTest` 을 사용.
   - `@Value` 는 생성자 주입 활용

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
### Branch
 - Fork된 branch에서 바로 main으로 합치지 말 것.
 - 개발 커밋들은 모두 **dev에서 관리**하고 Release가 발생할 때마다 main에 합치는 방식