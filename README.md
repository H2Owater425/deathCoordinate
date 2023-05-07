# 적용
1. `./gradlew clean build` 실행
2. `build/libs` 폴더의 jar 파일은 plugins 폴더에 `asm/build/libs` 폴더의 jar 파일은 실행 스크립트와 같은 디렉토리에 복사
3. 실행 스크립트의 구문을 `java -javaagent:asm-0.1.0.jar [여러 옵션들 eg -Xmx] [페이퍼 서버 파일].jar` 로 변경

# 오류
`Plugin [id: 'com.ldhdev.asm-ir-plugin', version: '1.0.0'] was not found in any of the following sources:` 오류가 난다면

<https://github.com/ldhdev916/asm-helper-base>을 clone 하고 `./gradlew publishToMavenLocal` 실행 후 다시 시도