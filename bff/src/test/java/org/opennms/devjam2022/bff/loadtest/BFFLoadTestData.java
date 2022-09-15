package org.opennms.devjam2022.bff.loadtest;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BFFLoadTestData {
    private long directCallV1;
    private long directCallV1byteTransfer;
    private long directCallV2;
    private long directCallV2byteTransfer;

    private long bffCallV1;
    private long bffCallV1byteTransfer;
    private long bffCallV2;
    private long bffCallV2byteTransfer;
}
