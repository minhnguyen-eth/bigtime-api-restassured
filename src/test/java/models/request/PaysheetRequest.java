package models.request;

import base.CommonRequest;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.Assert;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PaysheetRequest extends CommonRequest {

    public String createPaySheet(String token, String userId) {
        String body = """
                {
                  "id": "",
                  "name": "test api payroll",
                  "start_date": "2025-10-01",
                  "end_date": "2025-10-31",
                  "note": "",
                  "type": 0,
                  "status": 0,
                  "rangeMonth": "2025-10",
                  "isChooseAllUser": false,
                  "users_id": ["%s"],
                  "created_at": "2025-10-08 09:00:00",
                  "created_by": "admin"
                }
                """.formatted(userId);

        Response res = sendPost("/api/pay-sheet/create", token, body);
        assertSuccess(res, "Admin tạo bảng lương thành công!");
        String id = res.jsonPath().getString("data");
        Assert.assertNotNull(id, "Không lấy được paySheetId!");
        return id;
    }

    public void verifyPaySheetSalary(String token, String paySheetId,
                                     int expectedMainSalary,
                                     int expectedOvertimeSalary,
                                     int expectedInsurance,
                                     int expectedTax,
                                     int expectedTotalNeedPay
    ) {

        String endpoint = "/api/pay-sheet/get-user-by-pay-sheet/" + paySheetId;
        Response res = sendGet(endpoint, token);

//        System.out.println("PaySheet Response: " + res.asPrettyString());

        assertSuccess(res, "Kiểm tra lương thành công!");

        int main_salary = res.jsonPath().getInt("data[0].main_salary");
        int overtime_salary = res.jsonPath().getInt("data[0].overtime_salary");
        int total_insurance = res.jsonPath().getInt("data[0].total_insurance");
        int total_tax = res.jsonPath().getInt("data[0].total_tax");
        int total_need_pay = res.jsonPath().getInt("data[0].total_need_pay");


        Assert.assertEquals(main_salary, expectedMainSalary, "Lương chính không đúng!");
        Assert.assertEquals(overtime_salary, expectedOvertimeSalary, "Lương tăng ca không đúng!");
        Assert.assertEquals(total_insurance, expectedInsurance, "Tiền bảo hiểm không đúng!");
        Assert.assertEquals(total_tax, expectedTax, "Tiền thuế không đúng!");
        Assert.assertEquals(total_need_pay, expectedTotalNeedPay, "Tổng tiền cần trả không đúng!");

        System.out.println(
                        "Main salary = " + main_salary + "\n" +
                        "Overtime salary = " + overtime_salary + "\n" +
                        "Total insurance = " + total_insurance + "\n" +
                        "Total tax = " + total_tax + "\n" +
                        "Total need pay = " + total_need_pay
        );
    }


    //  Hàm submit pay sheet (gửi bảng lương)
    public void submitPaySheet(String token, String paySheetId) {
        String body = """
                {
                  "paysheet_id": "%s"
                }
                """.formatted(paySheetId);

        Response res = sendPost("/api/pay-sheet/submit-pay-sheet", token, body);
        assertSuccess(res, "Gửi bảng lương cho nhân viên thành công!");
    }

    //  Lấy phiếu lương mới nhất (của nhân viên)
    public String getLatestPayslipId(String token) {
        String currentMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        String endpoint = String.format(
                "/api/payslip/get-payslip-by-auth?page=1&itemsPerPage=20&start_date=%s",
                currentMonth
        );

        Response res = sendGet(endpoint, token);
        assertSuccess(res, "Lấy danh sách phiếu lương thành công!");

        JsonPath json = res.jsonPath();
        List<Object> payslips = json.getList("data.data");

        if (payslips == null || payslips.isEmpty()) {
            throw new RuntimeException("Không tìm thấy phiếu lương nào trong tháng hiện tại!");
        }

        //  Phiếu mới nhất thường nằm ở đầu danh sách
        String latestId = json.getString("data.data[0].id");
        System.out.println("Payslip ID mới nhất: " + latestId);
        return latestId;
    }

    //  Duyệt phiếu lương
    public void approvePayslip(String token, String payslipId, int status) {
        String body = """
                {
                  "id": "%s",
                  "status": %d
                }
                """.formatted(payslipId, status);

        Response res = sendPost("/api/payslip/approval-processes", token, body);
        assertSuccess(res, "Nhân viên duyệt phiếu lương thành công!");
    }

    //  Duyệt bảng lương tổng (approve salary)
    public void approveSalary(String token, String paySheetId) {
        String body = """
                {
                  "id": "%s"
                }
                """.formatted(paySheetId);

        Response res = sendPost("/api/pay-sheet/approve-salary", token, body);
        assertSuccess(res, "Duyệt bảng lương tổng thành công!");
    }

    public void makePayment(String token, String paySheetId, String paySlipId, int total, String note, int paymentType) {
        String body = """
                {
                  "paysheet_id": "%s",
                  "note": "%s",
                  "payment_type": %d,
                  "payment_history": [
                    {
                      "payslip_id": "%s",
                      "total": %d
                    }
                  ]
                }
                """.formatted(paySheetId, note, paymentType, paySlipId, total);

        Response res = sendPost("/api/payment-history/payment", token, body);
        assertSuccess(res, "Thanh toán bảng lương thành công!");
    }

    public void verifyPaySheetPayment(String token, String paySheetId, int expectedTotalPayment) {
        Response res = sendGet("/api/pay-sheet/get-by-id/" + paySheetId, token);

        assertSuccess(res, "Lấy thông tin bảng lương thành công!");

        int totalPayment = res.jsonPath().getInt("data.total_payment");
        int stillNeedPay = res.jsonPath().getInt("data.still_need_pay");
        int totalSalary = res.jsonPath().getInt("data.total_salary");

        System.out.println("Tổng lương: " + totalSalary);
        System.out.println("Đã thanh toán: " + totalPayment);
        System.out.println("Còn lại phải trả: " + stillNeedPay);

        // Kiểm tra tổng thanh toán đã đúng
        Assert.assertEquals(totalPayment, expectedTotalPayment,
                "Tổng tiền thanh toán chưa khớp với giá trị mong đợi!");
        Assert.assertEquals(stillNeedPay, totalSalary - totalPayment,
                "Giá trị còn lại phải trả chưa đúng!");
    }
}
