<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>

<html>
<head>
    <meta charset="UTF-8">
    <title>전투 테스트</title>
    <script>
        function battleTurnTest() {
            fetch("http://localhost:8080/testgame/CS/battle/turn")
                .then(res => res.json())
                .then(data => {
                    const log = document.getElementById("log");
                    log.innerHTML += `<p>${data.resultMessage}</p>`;
                    if (data.defeated) {
                        document.getElementById("battleBtn").disabled = true;
                        log.innerHTML += `<p><strong>전투 종료</strong></p>`;
                    }
                })
                .catch(err => console.error("에러 발생:", err));
        }
    </script>
</head>
<body>
    <h1>전투 테스트 (게이지 기반)</h1>
    <button id="battleBtn" onclick="battleTurnTest()">턴 실행</button>
    <div id="log" style="margin-top: 20px;"></div>
</body>
</html>
