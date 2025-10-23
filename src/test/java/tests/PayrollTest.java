package tests;

import base.BaseTest;
import models.request.*;
import org.testng.annotations.Test;
import java.util.Arrays;
import static constants.WorkingDayType.*;
import static db.DatabaseHelper.clearTestDataForUser;

public class PayrollTest extends BaseTest {

    PayrollRequest payrollRequest = new PayrollRequest();
    CheckDayRequest checkDay = new CheckDayRequest();
    ShiftPlanRequest shiftPlan = new ShiftPlanRequest();
    PaysheetRequest paysheet = new PaysheetRequest();
    OverTimeSubmissionRequest overTimeSubmission = new OverTimeSubmissionRequest();
    HolidayManagementRequest holidayManagement = new HolidayManagementRequest();
    UserRequest user = new UserRequest();


    // Tính lương ngày làm việc bình thường
    @Test(description = "E2E PayrollRequest Flow - Calculate salary based on normal working days")
    public void TC_001() {

        String WORK_DAY = "2025-10-08";

        clearTestDataForUser(dotenv.get("USER_ID"));
        user.updateUserForTaxTest(adminToken, 500000);
        shiftPlan.createShiftPlan(adminToken, dotenv.get("USER_ID"), WORK_DAY);
        checkDay.createCheckDay(adminToken, dotenv.get("USER_CODE"), WORK_DAY, Arrays.asList("08:00:00", "17:00:00"));
        checkDay.submitCheckDay(employeeToken, WORKING_NORMAL, dotenv.get("USER_ID"), WORK_DAY);
        checkDay.approveMonthlyWork(adminToken, dotenv.get("USER_ID"));
        payrollRequest.closePayroll(adminToken, dotenv.get("USER_ID"), WORK_DAY);

        String paySheetId = paysheet.createPaySheet(adminToken, dotenv.get("USER_ID"));
        paysheet.verifyPaySheetSalary(adminToken, paySheetId,
                500000,
                0,
                0,
                0,
                500000);
    }

    // Tính lương ngày làm việc bình thường + tăng ca
    @Test(description = "E2E PayrollRequest Flow -  Calculate salary for normal working days + overtime")
    public void TC_002() {

        String WORK_DAY = "2025-10-08";
        String DAY_OT = "2025-10-08";

        clearTestDataForUser(dotenv.get("USER_ID"));
        user.updateUserForTaxTest(adminToken, 11000000);
        shiftPlan.createShiftPlan(adminToken, dotenv.get("USER_ID"), WORK_DAY);

        checkDay.createCheckDay(adminToken, dotenv.get("USER_CODE"), WORK_DAY, Arrays.asList("08:00:00", "19:00:00"));


        String overtimeId = overTimeSubmission.createOvertimeTicket(
                employeeToken, DAY_OT, dotenv.get("USER_ID"), "Minh", WORK_DAY, "17:00", "19:00"
        );

        overTimeSubmission.submitOverSubmission(adminToken, overtimeId, 2); // 2 là trạng thái duyệt

        checkDay.submitCheckDay(employeeToken, WORKING_NORMAL, dotenv.get("USER_ID"), WORK_DAY);


        checkDay.approveMonthlyWork(adminToken, dotenv.get("USER_ID"));

        payrollRequest.closePayroll(adminToken, dotenv.get("USER_ID"), WORK_DAY);

        String paySheetId = paysheet.createPaySheet(adminToken, dotenv.get("USER_ID"));
        paysheet.verifyPaySheetSalary(adminToken, paySheetId,
                11000000,
                4125000,
                0,
                0,
                15125000);
    }

    // Tính lương ngày nghỉ lễ
    @Test(description = "E2E PayrollRequest Flow - Calculate salary with holidays")
    public void TC_003() {

        String WORK_DAY = "2025-10-08";
        clearTestDataForUser(dotenv.get("USER_ID"));
        user.updateUserForTaxTest(adminToken, 11000000);
        shiftPlan.createShiftPlan(adminToken, dotenv.get("USER_ID"), WORK_DAY);
        holidayManagement.createHoliday(adminToken, "Test API Holiday", WORK_DAY, WORK_DAY);
        checkDay.submitCheckDay(employeeToken, ADVANCED_HOLIDAY, dotenv.get("USER_ID"), WORK_DAY);
        checkDay.approveMonthlyWork(adminToken, dotenv.get("USER_ID"));
        payrollRequest.closePayroll(adminToken, dotenv.get("USER_ID"), WORK_DAY);

        String paySheetId = paysheet.createPaySheet(adminToken, dotenv.get("USER_ID"));
        paysheet.verifyPaySheetSalary(adminToken, paySheetId,
                11000000,
                0,
                0,
                0,
                11000000);
    }

    //Tính lương có ngày chủ nhật (Có lịch làm việc)
    @Test(description = "E2E PayrollRequest Flow - Salary calculation with Sunday (With working schedule)")
    public void TC_004() {

        String WORK_DAY = "2025-10-05";  // 2025-10-05  is a Sunday
        clearTestDataForUser(dotenv.get("USER_ID"));
        user.updateUserForTaxTest(adminToken, 500000);
        shiftPlan.createShiftPlan(adminToken, dotenv.get("USER_ID"), WORK_DAY);
        checkDay.createCheckDay(adminToken, dotenv.get("USER_CODE"), WORK_DAY, Arrays.asList("08:00:00", "17:00:00"));
        checkDay.submitCheckDay(employeeToken, ADVANCED_HOLIDAY, dotenv.get("USER_ID"), WORK_DAY);
        checkDay.approveMonthlyWork(adminToken, dotenv.get("USER_ID"));
        payrollRequest.closePayroll(adminToken, dotenv.get("USER_ID"), WORK_DAY);

        String paySheetId = paysheet.createPaySheet(adminToken, dotenv.get("USER_ID"));
        paysheet.verifyPaySheetSalary(adminToken, paySheetId,
                1000000,
                0,
                0,
                0,
                1000000);
    }

    // Tính lương ngày làm việc ngày lễ + tăng ca
    @Test(description = "E2E PayrollRequest Flow -  Calculate salary for normal working days + overtime")
    public void TC_005() {

        String WORK_DAY = "2025-10-08";
        String DAY_OT = "2025-10-08";

        clearTestDataForUser(dotenv.get("USER_ID"));
        user.updateUserForTaxTest(adminToken, 500000);
        shiftPlan.createShiftPlan(adminToken, dotenv.get("USER_ID"), WORK_DAY);
        holidayManagement.createHoliday(adminToken, "Test API Holiday", WORK_DAY, WORK_DAY);

        checkDay.createCheckDay(adminToken, dotenv.get("USER_CODE"), WORK_DAY, Arrays.asList("08:00:00", "19:00:00"));


        String overtimeId = overTimeSubmission.createOvertimeTicket(
                employeeToken, DAY_OT, dotenv.get("USER_ID"), "Minh", WORK_DAY, "17:00", "19:00"
        );

        overTimeSubmission.submitOverSubmission(adminToken, overtimeId, 2); // 2 là trạng thái duyệt

        checkDay.submitCheckDay(employeeToken, WORKING_NORMAL, dotenv.get("USER_ID"), WORK_DAY);


        checkDay.approveMonthlyWork(adminToken, dotenv.get("USER_ID"));

        payrollRequest.closePayroll(adminToken, dotenv.get("USER_ID"), WORK_DAY);

        String paySheetId = paysheet.createPaySheet(adminToken, dotenv.get("USER_ID"));
        paysheet.verifyPaySheetSalary(adminToken, paySheetId,
                1500000,
                562500,
                0,
                0,
                2062500 );
    }
}