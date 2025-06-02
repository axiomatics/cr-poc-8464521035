package com.myorg.alfa;

import org.junit.jupiter.api.extension.RegisterExtension;

import com.axiomatics.cr.alfa.test.junit.AlfaExtension;

public class permitAlignedUserAndRecordGradeLevelTest {
    @RegisterExtension
    public AlfaExtension alfa = new AlfaExtension().withMainPolicy("school.Main");

    
}
