package org.opennms.devjam2022.apiserver.model.utils;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

public class ModelUtilTest {
    @Test
    public void testAllIdArePositive() {
        for (long l = 0; l < 100000l; l++) {
            assertThat(ModelUtil.generateId()).doesNotContain("-");
        }
    }
}
