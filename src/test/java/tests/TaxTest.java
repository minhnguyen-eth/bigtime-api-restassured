package tests;

import base.BaseTest;
import models.request.*;
import org.testng.annotations.Test;

import java.util.Arrays;

import static constants.WorkingDayType.WORKING_NORMAL;
import static db.DatabaseHelper.clearTestDataForUser;

public class TaxTest extends BaseTest {

    String WORK_DAY = "2025-10-08";
    int TOTAL_AMOUNT = 300000;

    PayrollRequest payrollRequest = new PayrollRequest();
    CheckDayRequest checkDay = new CheckDayRequest();
    ShiftPlanRequest shiftPlan = new ShiftPlanRequest();
    PaysheetRequest paysheet = new PaysheetRequest();
    OverTimeSubmissionRequest overTimeSubmission = new OverTimeSubmissionRequest();
    HolidayManagementRequest holidayManagement = new HolidayManagementRequest();
    UserRequest user = new UserRequest();


    // Tính thuế nhân viên có 1 người phụ thuộc
    // Lương chính: 20,000,000
    // Đóng bảo hiểm: 2,100,000
    // Trừ thuế bậc 1: 125,000
    // Thực nhận: 17,775,000
    @Test(description = "E2E Tax Flow - Calculate tax for employee with 1 dependent")
    public void TC_001() {

        String WORK_DAY = "2025-10-08";

        clearTestDataForUser(dotenv.get("USER_ID_1DEPENDENT"));
        shiftPlan.createShiftPlan(adminToken, dotenv.get("USER_ID_1DEPENDENT"), WORK_DAY);
        checkDay.createCheckDay(adminToken, dotenv.get("USER_CODE_1DEPENDENT"), WORK_DAY, Arrays.asList("08:00:00", "17:00:00"));
        checkDay.submitCheckDay(employeeToken, WORKING_NORMAL, dotenv.get("USER_ID_1DEPENDENT"), WORK_DAY);
        checkDay.approveMonthlyWork(adminToken, dotenv.get("USER_ID_1DEPENDENT"));
        payrollRequest.closePayroll(adminToken, dotenv.get("USER_ID_1DEPENDENT"), WORK_DAY);

        String paySheetId = paysheet.createPaySheet(adminToken, dotenv.get("USER_ID_1DEPENDENT"));
        paysheet.verifyPaySheetSalary(adminToken, paySheetId,
                20000000,
                0,
                2100000, 
                125000,
                17775000 );
    }

    // Tính thuế nhân viên không có người phụ thuộc
    // Lương chính: 20,000,000
    // Đóng bảo hiểm: 2,100,000
    // Lương chịu thuế: 20,000,000 - bảo hiểm - (11,000,000 + 0 NPT * 4,400,000) = 6.900.000
    // Trừ thuế bậc 1: 5,000,000 * 5% = 250,000.
    // Trừ thuế bậc 2: 1,900,000 * 10% = 190,000.
    // Thực nhận: 17.460.000
    @Test(description = "E2E Tax Flow - Calculate tax for employee with no dependent")
    public void TC_002() {

        String WORK_DAY = "2025-10-08";

        clearTestDataForUser(dotenv.get("USER_ID_NO_DEPENDENT"));
        shiftPlan.createShiftPlan(adminToken, dotenv.get("USER_ID_NO_DEPENDENT"), WORK_DAY);
        checkDay.createCheckDay(adminToken, dotenv.get("USER_CODE_NO_DEPENDENT"), WORK_DAY, Arrays.asList("08:00:00", "17:00:00"));
        checkDay.submitCheckDay(employeeToken, WORKING_NORMAL, dotenv.get("USER_ID_NO_DEPENDENT"), WORK_DAY);
        checkDay.approveMonthlyWork(adminToken, dotenv.get("USER_ID_NO_DEPENDENT"));
        payrollRequest.closePayroll(adminToken, dotenv.get("USER_ID_NO_DEPENDENT"), WORK_DAY);

        String paySheetId = paysheet.createPaySheet(adminToken, dotenv.get("USER_ID_NO_DEPENDENT"));
        paysheet.verifyPaySheetSalary(adminToken, paySheetId,
                20000000,
                0,
                2100000,
                440000,
                17460000 );
    }

    // Tính thuế bậc cao nhất cho nhân viên có lương cao và không có người phụ thuộc
    @Test(description = "E2E Tax Flow - Calculate highest tax bracket for employee with high salary and no dependent")
    public void TC_003() {

        String WORK_DAY = "2025-10-08";

        clearTestDataForUser(dotenv.get("USER_ID_HIGH_SALARY_NO_DEPENDENT"));
        user.updateUserForTaxTest(adminToken, 120000000);
        shiftPlan.createShiftPlan(adminToken, dotenv.get("USER_ID_HIGH_SALARY_NO_DEPENDENT"), WORK_DAY);
        checkDay.createCheckDay(adminToken, dotenv.get("USER_CODE_HIGH_SALARY_NO_DEPENDENT"), WORK_DAY, Arrays.asList("08:00:00", "17:00:00"));
        checkDay.submitCheckDay(employeeToken, WORKING_NORMAL, dotenv.get("USER_ID_HIGH_SALARY_NO_DEPENDENT"), WORK_DAY);
        checkDay.approveMonthlyWork(adminToken, dotenv.get("USER_ID_HIGH_SALARY_NO_DEPENDENT"));
        payrollRequest.closePayroll(adminToken, dotenv.get("USER_ID_HIGH_SALARY_NO_DEPENDENT"), WORK_DAY);

        String paySheetId = paysheet.createPaySheet(adminToken, dotenv.get("USER_ID_HIGH_SALARY_NO_DEPENDENT"));
        paysheet.verifyPaySheetSalary(adminToken, paySheetId,
                100000000,
                0,
                0,
                21300000 ,
                78700000  );
    }
}