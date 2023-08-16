package com.jkqj.nats;

import java.util.Optional;

/**
 * @author xuweizhe@reta-inc.com
 * @date 2022/3/18
 * @description
 */
public enum JkqjNatsSubject implements Subject {
    //infra
    INFRA_CV_PERSON_CREATED("infra", "cv.person.created"),
    INFRA_MOD_ISSUE("infra", "infra.mod.issue", "infra"),
    INFRA_SCRAPED_COMPANY("infra", "infra.scraped.company", "infra"),

    //user-center
    UC_USER_CREATED("uc", "user.created"),
    UC_USER_DELETED("uc", "user.deleted"),
    UC_BIZ_USER_SUBMIT_AUDIT("uc", "biz.user.submit.audit"),
    UC_SCAN_CODE_SIGN_UP("uc", "scan.code.sign.up"),

    //b-server
    BSERVER_BIZ_USER_AUDIT_RESULT("bserver", "biz.user.audit.result"),
    BSERVER_QUESTION_CREATED("bserver", "question.created"),
    BSERVER_MOD_ISSUE("bserver", "bserver.mod.issue", "bserver"),
    BSERVER_CV_PDF_TO_PNGS("bserver", "bserver.cv.pdf.to.pngs"),

    //sirius
    SIRIUS_MOD_ISSUE("sirius", "sirius.mod.issue", "sirius"),

    // job
    JOB_APPEND_EVALUATORS("job", "evaluators.append", "job"),

    // galaxy
    GALAXY_MOD_ISSUE("galaxy", "galaxy.mod.issue", "galaxy");

    private final String category;
    private final String name;
    private String group;

    JkqjNatsSubject(String category, String name) {
        this.category = category;
        this.name = name;
    }

    JkqjNatsSubject(String category, String name, String group) {
        this.category = category;
        this.name = name;
        this.group = group;
    }

    @Override
    public String getCategory() {
        return this.category;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Optional<String> getGroup() {
        return Optional.ofNullable(this.group);
    }
}
