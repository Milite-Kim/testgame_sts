use testgame;

CREATE TABLE Artifact (
    ID INT PRIMARY KEY,
    Name VARCHAR(50) NOT NULL,
    Job VARCHAR(20) NOT NULL,
    Session VARCHAR(20) NOT NULL,
    Effect TEXT NOT NULL,
    Description TEXT NOT NULL,
    EffectType ENUM('BATTLE_RESET', 'FOR_ONCE', 'PASSIVE') DEFAULT 'PASSIVE',
    -- BATTLE_RESET: 매 전투마다 리셋
    -- FOR_ONCE: 한 번 쓰면 영구 소모
    -- PASSIVE: 지속 효과
    isActive boolean
);

INSERT INTO Artifact (ID, Name, Job, Session, Effect, Description, EffectType, isActive) VALUES
(101, '파이터 길드 메달', 'Common', 'Common', '무속성 스킬 카드의 데미지 2 증가', '격투 길드의 싸움에서 이긴자에게 주어지는 증표', 'PASSIVE', TRUE),
(102, '작열하는 용암석', 'Common', 'Common', '불속성 스킬 카드의 데미지 2 증가', '아직 식지 않은 용암', 'PASSIVE', TRUE),
(103, '푸른 빛의 삼지창', 'Common', 'Common', '물속성 스킬 카드의 데미지 2 증가', '뾰족하진 않지만 아프긴 할 것 같은 삼지창', 'PASSIVE', TRUE),
(104, '드루이드의 벨트', 'Common', 'Common', '풀속성 스킬 카드의 데미지 2 증가', '풀과 나뭇가지로 엮어만든 벨트. 중간 중간 꽃이 있다', 'PASSIVE', TRUE),
(105, '원소의 돌', 'Common', 'Common', '우세 상성 공격 시 배율 10% 증가', '잡을 때마다 다른 느낌이 드는 신비한 돌', 'PASSIVE', TRUE),
(106, '마법사의 부적', 'Magicial', 'Common', '약세 상성 공격 시 배율 10% 증가', '마법사는 자신의 제자가 떠나기 전에 스스로 만든 부적을 건네주는 전통이 있다', 'PASSIVE', TRUE),
(107, '금단의 주문서', 'Magicial', 'Event', '우세 상성 공격 시 배율 20% 증가/약세 상성 공격 시 배율 20% 감소', '오래 전 분실되었다고 알려진 금단서', 'PASSIVE', TRUE),
(108, '잘 마른 나무', 'Common', 'Common', '화상 데미지 1 증가', '아주 잘 말랐고, 아주 잘 탈 것 같다', 'PASSIVE', TRUE),
(109, '바늘 달린 독 장치', 'Thief', 'Event', '중독 상태이상 적용 시, 기존에 적용된 중독 스택의 절반만큼 피해를 가함', '누가봐도 독이 발려있다', 'PASSIVE', TRUE),
(110, '어두운 망치', 'Thief', 'Common', '기절 상태이상 지속 시간 1턴 증가', '왜 망치를 잘 안보이게 검게 칠한거지? 아', 'PASSIVE', TRUE),
(111, '부서진 칼날', 'Common', 'Common', '피격 시, 공격자에게 2의 피해를 가함', '"이거 그래도 날카롭지 않아?"', 'PASSIVE', TRUE),
(112, '회색 망토', 'Common', 'Common', '회피율 5% 증가', '별 거 아닌 망토지만 적의 시야를 혼란스럽게 만드는데는 충분하다', 'PASSIVE', TRUE),
(113, '흐릿한 렌즈', 'Common', 'Common', '적의 회피율 5% 감소', '없는 것보다는 나은 렌즈', 'PASSIVE', TRUE),
(114, '과부하 결정', 'Common', 'Common', '첫 턴에 하는 공격은 확정 명중', '적의 위치 이동을 방해할 수 있는 결정. 다만 한 번 쓰면 꽤 오래 충전해야할 거 같다', 'BATTLE_RESET', TRUE),
(115, '미끄러운 가죽 보호대', 'Common', 'Common', '전투마다 처음에 받는 피해를 무효화함', '미끌미끌해!!', 'BATTLE_RESET', TRUE),
(116, '바다의 심장', 'Warrior', 'Event', '매 턴 5 회복', '시원한 기운이 흘러나오는 돌', 'PASSIVE', TRUE),
(117, '심해의 진주', 'Common', 'Common', '공격 적중 시 2 회복', '바다 깊은 곳에서 발견되는 진주. 반짝거린다', 'PASSIVE', TRUE),
(118, '검은 산고', 'Warrior', 'Common', '공격 시 3 회복', '그 산호 안에는 다양한 것들이 있다. 가령 흡혈하는 벌레라던가', 'PASSIVE', TRUE),
(119, '불사조의 하얀 깃털', 'Common', 'Common', '1회에 한하여, 죽음에 이르는 피해를 받았을 때, 체력 50% 회복하며 부활', '"불사조의 깃털이라고? 에이~ 그런게 어디있나~"', 'FOR_ONCE', TRUE),
(120, '불사조의 빛바랜 깃털', 'Common', 'Unique', '그 힘을 잃은 불사조의 깃털', '"세상에 내가 살아있잖아?"', 'PASSIVE', FALSE),
(121, '그림자 생성 장치', 'Common', 'Unique', '공격 횟수가 1회 증가', '행동을 어설프게 따라하는 그림자를 발생시키는 장치', 'PASSIVE', TRUE);