package tests;

import base.BaseTest;
import models.request.*;
import org.testng.annotations.Test;

import java.util.Arrays;
import static constants.ApprovalStatus.*;
import static constants.WorkingDayType.*;
import static db.DatabaseHelper.clearTestDataForUser;

public class DebtTest extends BaseTest {

    PayrollRequest payrollRequest = new PayrollRequest();
    CheckDayRequest checkDay = new CheckDayRequest();
    ShiftPlanRequest shiftPlan = new ShiftPlanRequest();
    PaysheetRequest paysheet = new PaysheetRequest();
    OverTimeSubmissionRequest overTimeSubmission = new OverTimeSubmissionRequest();
    HolidayManagementRequest holidayManagement = new HolidayManagementRequest();
    DebtRequest debt = new DebtRequest();
    UserRequest user = new UserRequest();

    // Calculate salary when there is an advance
    @Test(description = "E2E Debt Flow - Calculate salary when there is an advance")
    public void TC_001() {

        String WORK_DAY = "2025-10-08";
        int TOTAL_AMOUNT = 500000;

        clearTestDataForUser(dotenv.get("USER_ID"));
        user.updateUserForTaxTest(adminToken, 500000);

        // Step 1: Create a new debt
        debt.createDebt(adminToken, dotenv.get("USER_ID"), "Test api", 200000, "admin");

        // Step 2: Get the latest debt ID
        String latestDebtId = debt.getLatestDebtId(adminToken, dotenv.get("USER_ID"));
        System.out.println("Debt ID: " + latestDebtId);

        // Step 3: Submit the debt
        debt.submitDebt(adminToken, latestDebtId);

        // Step 4: Approve the debt
        debt.approveDebt(employeeToken, latestDebtId, 2);

        // Step 5: Close payroll for the month
        shiftPlan.createShiftPlan(adminToken, dotenv.get("USER_ID"), WORK_DAY);
        checkDay.createCheckDay(adminToken, dotenv.get("USER_ID"), WORK_DAY, Arrays.asList("08:00:00", "17:00:00"));
        checkDay.submitCheckDay(employeeToken, WORKING_NORMAL, dotenv.get("USER_ID"), WORK_DAY);
        checkDay.approveMonthlyWork(adminToken, dotenv.get("USER_ID"));
        payrollRequest.closePayroll(adminToken, dotenv.get("USER_ID"), WORK_DAY);

        // Step 6: Create paysheet
        String paySheetId = paysheet.createPaySheet(adminToken, dotenv.get("USER_ID"));

        // Step 7: Submit paysheet
        paysheet.submitPaySheet(adminToken, paySheetId);

        // Step 8: Get latest payslip ID
        String payslipId = paysheet.getLatestPayslipId(employeeToken);

        // Step 9: Approve payslip (status = 1: approved)
        paysheet.approvePayslip(employeeToken, payslipId, APPROVED_DEBT);

        // Step 10: Admin approves paysheet
        paysheet.approveSalary(adminToken, paySheetId);

        // Step 11: Make Payment
        paysheet.makePayment(adminToken, paySheetId, payslipId, 500000, "", 0);

        // Step 12: Verify payment - Expected: (Lương 500.000 - tạm ứng 200.000) total_payment = 300.000
        paysheet.verifyPaySheetPayment(adminToken, paySheetId, TOTAL_AMOUNT);

        debt.verifyDebtValue(adminToken, "Minh", 0);

    }
}