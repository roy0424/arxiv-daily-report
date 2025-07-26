package com.example.arxivdailyreport.entity;

import com.example.arxivdailyreport.exception.BusinessException;
import com.example.arxivdailyreport.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ArxivCategory {
    CS("cs", "컴퓨터공학"),
    AI("cs.AI", "인공지능"),
    AR("cs.AR", "하드웨어 아키텍처"),
    CC("cs.CC", "계산 복잡도"),
    CE("cs.CE", "계산 과학·공학·금융"),
    CG("cs.CG", "계산 기하학"),
    CL("cs.CL", "자연어 처리"),
    CR("cs.CR", "암호학 및 보안"),
    CV("cs.CV", "컴퓨터 비전 및 패턴 인식"),
    CY("cs.CY", "컴퓨터와 사회"),
    DB("cs.DB", "데이터베이스"),
    DC("cs.DC", "분산·병렬·클러스터 컴퓨팅"),
    DL("cs.DL", "디지털 라이브러리"),
    DM("cs.DM", "이산 수학"),
    DS("cs.DS", "자료구조 및 알고리즘"),
    ET("cs.ET", "신기술"),
    FL("cs.FL", "형식 언어 및 오토마타 이론"),
    GL("cs.GL", "일반 문헌 및 서베이"),
    GR("cs.GR", "컴퓨터 그래픽스"),
    GT("cs.GT", "컴퓨터 과학과 게임 이론"),
    HC("cs.HC", "인간-컴퓨터 상호작용"),
    IR("cs.IR", "정보 검색"),
    IT("cs.IT", "정보 이론"),
    LG("cs.LG", "머신러닝"),
    LO("cs.LO", "컴퓨터 과학의 논리"),
    MA("cs.MA", "다중 에이전트 시스템"),
    MM("cs.MM", "멀티미디어"),
    MS("cs.MS", "수학적 소프트웨어"),
    NA("cs.NA", "수치 해석"),
    NE("cs.NE", "신경망 및 진화 컴퓨팅"),
    NI("cs.NI", "네트워크 및 인터넷 아키텍처"),
    OH("cs.OH", "기타 컴퓨터 과학"),
    OS("cs.OS", "운영체제"),
    PF("cs.PF", "성능 분석"),
    PL("cs.PL", "프로그래밍 언어"),
    RO("cs.RO", "로보틱스"),
    SC("cs.SC", "기호 계산"),
    SD("cs.SD", "사운드 및 오디오 처리"),
    SE("cs.SE", "소프트웨어 공학"),
    SI("cs.SI", "사회 및 정보 네트워크"),
    SY("cs.SY", "시스템 및 제어");

    private final String endpoint;
    private final String name;

    public static ArxivCategory fromEndpoint(String endpoint) {
        for (ArxivCategory c: values()) {
            if (c.getEndpoint().equalsIgnoreCase(endpoint)) {
                return c;
            }
        }
        throw new BusinessException(ErrorCode.INVALID_CATEGORY);
    }

}
