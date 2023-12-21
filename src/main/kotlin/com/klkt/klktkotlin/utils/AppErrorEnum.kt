package com.klkt.klktkotlin.utils

enum class AppErrorEnum(val code: Int, val value:String) {
      LOGIN_THANH_CONG(1000, "Xác thực người dùng thành công")
    , LOGIN_LOI_XAC_THUC(9999, "Lỗi xác thực người dùng")
    , LOGIN_LOI_TOKEN_KHONG_HOP_LE(1002, "Lỗi Token không hợp lệ")
    , LOGIN_LOI_TOKEN_KHONG_XAC_DINH(1003, "Lỗi Token không xác định")
    , LOGIN_LOI_DL_DAU_VAO(1097, "Dữ liệu đầu vào không hợp lệ")
    , LOGIN_TIME_OUT(1098, "Lỗi Timeout")
    , LOGIN_SYSTEM_ERROR(1099, "Lỗi hệ thống")

    , TCTTCD_THANH_CONG(1100, "Tìm kiếm thành công")
    , TCTTCD_KHONG_CO_DL(1101, "Không tìm thấy kết quả")
    , TCTTCD_LOI_C06(1102, "Lỗi trả về của C06")
    , TCTTCD_LOI_TRUC_SS(1103, "Lỗi trả về từ trục SS")
    , TCTTCD_LOI_SO_HIEU_CB(1191, "Số hiệu cán bộ không hợp lệ")
    , TCTTCD_LOI_MA_HE_THONG(1192, "Mã hệ thống không hợp lệ")
    , TCTTCD_LOI_DL_DAU_VAO(1197, "Dữ liệu đầu vào không hợp lệ")
    , TCTTCD_TIME_OUT(1198, "Lỗi Timeout")
    , TCTTCD_SYSTEM_ERROR(1199, "Lỗi hệ thống")

    , ERR_THANH_CONG(0, "Thành công")
    , ERR_KHONG_CO_DL(1, "Không tìm thấy dữ liệu")
    , ERR_SYSTEM_ERROR(99, "Lỗi hệ thống")
    , ERR_AUTH_ERROR(99, "Lỗi xác thực")
    , ERR_INPUT_INVALID(3, "Dữ liệu đầu vào không hợp lệ")

}