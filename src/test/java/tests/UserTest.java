package tests;

import base.BaseTest;
import models.request.*;
import org.testng.annotations.Test;
import static db.DatabaseHelper.clearTestDataForUser;

public class UserTest extends BaseTest {

    String WORK_DAY = "2025-10-08";

    PayrollRequest payrollRequest = new PayrollRequest();
    CheckDayRequest checkDay = new CheckDayRequest();
    ShiftPlanRequest shiftPlan = new ShiftPlanRequest();
    PaysheetRequest paysheet = new PaysheetRequest();
    OverTimeSubmissionRequest overTimeSubmission = new OverTimeSubmissionRequest();
    HolidayManagementRequest holidayManagement = new HolidayManagementRequest();
    DebtRequest debt = new DebtRequest();
    UserRequest user = new UserRequest();


    // Update salary
    @Test(description = "E2E Debt Flow - Calculate salary when there is an advance")
    public void TC_001() {

        clearTestDataForUser(dotenv.get("USER_ID"));
        user.updateUserForTaxTest(adminToken, 500000);
    }
}