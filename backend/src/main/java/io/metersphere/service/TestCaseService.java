package io.metersphere.service;


import io.metersphere.base.domain.*;
import io.metersphere.base.mapper.TestCaseMapper;
import io.metersphere.base.mapper.TestPlanMapper;
import io.metersphere.base.mapper.TestPlanTestCaseMapper;
import io.metersphere.base.mapper.ext.ExtTestCaseMapper;
import io.metersphere.controller.request.testcase.QueryTestCaseRequest;
import io.metersphere.dto.TestPlanCaseDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
public class TestCaseService {

    @Resource
    TestCaseMapper testCaseMapper;

    @Resource
    ExtTestCaseMapper extTestCaseMapper;

    @Resource
    TestPlanTestCaseMapper testPlanTestCaseMapper;

    @Resource
    TestPlanMapper testPlanMapper;

    public void addTestCase(TestCaseWithBLOBs testCase) {
        testCase.setId(UUID.randomUUID().toString());
        testCase.setCreateTime(System.currentTimeMillis());
        testCase.setUpdateTime(System.currentTimeMillis());
        testCaseMapper.insert(testCase);
    }

    public List<TestCase> getTestCaseByNodeId(List<Integer> nodeIds) {
        TestCaseExample testCaseExample = new TestCaseExample();
        testCaseExample.createCriteria().andNodeIdIn(nodeIds);
        return testCaseMapper.selectByExample(testCaseExample);
    }

    public List<TestCaseWithBLOBs> getTestCase(String testCaseId) {
        TestCaseExample testCaseExample = new TestCaseExample();
        testCaseExample.createCriteria().andIdEqualTo(testCaseId);
        return testCaseMapper.selectByExampleWithBLOBs(testCaseExample);
    }

    public int editTestCase(TestCaseWithBLOBs testCase) {
        testCase.setUpdateTime(System.currentTimeMillis());
        return testCaseMapper.updateByPrimaryKeySelective(testCase);
    }

    public int deleteTestCase(String testCaseId) {
        return testCaseMapper.deleteByPrimaryKey(testCaseId);
    }

    public List<TestCaseWithBLOBs> listTestCase(QueryTestCaseRequest request) {
        TestCaseExample testCaseExample = new TestCaseExample();
        TestCaseExample.Criteria criteria = testCaseExample.createCriteria();
        if ( StringUtils.isNotBlank(request.getName()) ) {
            criteria.andNameLike("%" + request.getName() + "%");
        }
        if ( StringUtils.isNotBlank(request.getProjectId()) ) {
            criteria.andProjectIdEqualTo(request.getProjectId());
        }
        if ( request.getNodeIds() != null && request.getNodeIds().size() > 0) {
            criteria.andNodeIdIn(request.getNodeIds());
        }
        return testCaseMapper.selectByExampleWithBLOBs(testCaseExample);
    }

    public List<TestCase> getTestCaseNames(QueryTestCaseRequest request) {

        if ( StringUtils.isNotBlank(request.getPlanId()) ) {
            TestPlan testPlan = testPlanMapper.selectByPrimaryKey(request.getPlanId());
            request.setProjectId(testPlan.getProjectId());
        }

        return extTestCaseMapper.getTestCaseNames(request);
    }

    public List<TestPlanCaseDTO> getTestPlanCases(QueryTestCaseRequest request) {
        return extTestCaseMapper.getTestPlanTestCases(request);
    }
}
