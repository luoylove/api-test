package com.ly.core.parse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author: luoy
 * @Date: 2020/4/20 9:37.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DataEntity {
    List<TestCase> testCase;
}
