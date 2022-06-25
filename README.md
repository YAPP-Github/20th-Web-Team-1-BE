# 20th-Web-Team-1-BE
[20th] Web 1팀 BackEnd

### 프로젝트 소개
Betree  
## 팀원
- [강지수](https://github.com/kang-jisu)
- [김수빈](https://github.com/suubinkim)

## 기술 스택
- java 8
- Spring Boot, gradle
- mysql
- junit5

## 아키텍처

## git 컨벤션

### 브랜치 전략 - git flow
- main : 메인 브랜치, 실제 배포용  
- develop : 개발 브랜치, 배포 전 임시 API 서버 배포용   
- feature : 작업 브랜치  
  - 네이밍 : feature/{간단한 설명}  

1. 관련 이슈 생성 및 할당 
2. feature 브랜치 작업 후 PR 요청      
3. 코드리뷰 후 develop 브랜치에 머지 
4. 머지된 브랜치는 삭제  

### 커밋 메시지 및 라벨 구분 
```java
feat : 새로운 기능에 대한 커밋
fix  : 수정에 대한 커밋
bug  : 버그에 대한 커밋
build : 빌드 관련 파일 수정에 대한 커밋
ci/cd : 배포 커밋
docs : 문서 수정에 대한 커밋
style : 코드 스타일 혹은 포맷 등에 관한 커밋
refactor :  코드 리팩토링에 대한 커밋
test : 테스트 코드 수정에 대한 커밋
```

## 코딩 컨벤션

1. Camel Case 사용
    - lower Camel Case
2. 함수의 경우 동사+명사 사용
    - ex) getInformation()
3. 약어는 되도록 사용하지 않는다.
4. static final 상수는 대문자,언더바 사용
5. 한줄짜리도 괄호 쓰기 (ex. if문)
6. 인텔리제이 자동 정렬 사용

- 엔티티와 DTO 분리
    - @Getter
    - @NoArgsConstructor(protected)
    - @Builder
    - DTO<->Entity 변환 메서드 DTO 클래스에 구현해 사용 

## 배포 방법
> 네이버클라우드 서버, DEVELOP 브랜치 테스트용 수동 배포 기준  
> [TODO] CI/CD 적용 필요 

1. 네이버 클라우드 서버 ssh 접속
```bash
$ ssh root@{serverIp} -p {port}
root@{serverIp}'s password:
```

2. 프로젝트 위치로 이동 
```bash
$ cd test/20th-Web-Team-1-BE/
```

3. git pull
```bash
$ git pull origin develop
```

4. 빌드
> [참고] ./gradlew 파일 +x 설정 필요  
> ./gradlew build --stacktrace -info 로 로그 볼 수 있음  
> 테스트코드 실패로 오류나는것 있으면 임시로 파일 삭제후 사용 (정식 배포 이후에는 이렇게 x)
```bash
$ ./gradlew build

... 
BUILD SUCCESSFUL in 18s
7 actionable tasks: 6 executed, 1 up-to-date
```

`/build/libs/beTree-0.0.1-SNAPSHOT.jar ` 파일이 생성된다.

5. 프로젝트 재배포
```bash
# 현재 실행중인 프로젝트 프로세스 ID 확인 및 종료
$ ps -ef | grep java
root      78594      1  0 15:29 ?        00:00:19 java -jar build/libs/beTree-0.0.1-SNAPSHOT.jar

$ kill -9 78594

# 이전 nohup 로그 파일 백업
# mv nohup.out ../log/nohup{현재날짜,일시}.out
$ mv nohup.out ../log/nohup2206251606.out

# nohup으로 백그라운드 실행
$ nohup java -jar ./build/libs/beTree-0.0.1-SNAPSHOT.jar &

# cat nohup.out으로 현재 로그 확인할 수 있다. 
$ cat nohup.out

# 터미널 종료시 exit로 종료
$ exit 
```

6. 서버 배포되었는지 확인  
스웨거 주소는 http://{serverIp}:8080/swagger-ui/index.html#/
   
