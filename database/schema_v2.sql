-- =========================
-- 1. BẢNG KHÔNG PHỤ THUỘC FK
-- =========================

CREATE TABLE public.vai_tro (
                                id_vaitro integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                ten_vaitro character varying NOT NULL UNIQUE
);

CREATE TABLE public.khach_hang (
                                   id_kh integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                   ho_ten character varying NOT NULL,
                                   sdt character varying,
                                   cccd character varying UNIQUE,
                                   email character varying
);

CREATE TABLE public.phong (
                              id_phong integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                              ten_phong character varying NOT NULL,
                              loai_phong character varying,
                              suc_chua integer,
                              gia_phong numeric,
                              trang_thai character varying
);

CREATE TABLE public.dich_vu (
                                id_dichvu integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                ten_dich_vu character varying NOT NULL,
                                don_gia numeric NOT NULL
);

CREATE TABLE public.hoa_don (
                                id_hoadon integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                tong_tien numeric DEFAULT 0,
                                trang_thai character varying,
                                ngay_lap timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE public.users (
                              id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                              email character varying NOT NULL UNIQUE,
                              name character varying NOT NULL
);

-- =========================
-- 2. TÀI KHOẢN / LỄ TÂN
-- =========================

CREATE TABLE public.tai_khoan (
                                  id_taikhoan integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                  ten_dang_nhap character varying NOT NULL UNIQUE,
                                  mat_khau character varying NOT NULL,
                                  ho_ten character varying NOT NULL,
                                  id_vaitro integer,
                                  id_nhan_vien integer,
                                  chuc_vu character varying NOT NULL,
                                  email character varying NOT NULL,
                                  gioi_tinh character varying NOT NULL,
                                  ngay_sinh character varying,
                                  so_dien_thoai character varying NOT NULL,
                                  trang_thai character varying DEFAULT 'Hoạt động',

                                  CONSTRAINT tai_khoan_id_vaitro_fkey
                                      FOREIGN KEY (id_vaitro) REFERENCES public.vai_tro(id_vaitro)
);

CREATE TABLE public.le_tan (
                               id_letan integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                               id_taikhoan integer,

                               CONSTRAINT le_tan_id_taikhoan_fkey
                                   FOREIGN KEY (id_taikhoan) REFERENCES public.tai_khoan(id_taikhoan)
);

-- =========================
-- 3. ĐẶT PHÒNG
-- =========================

CREATE TABLE public.dat_phong (
                                  ma_dat_phong character varying PRIMARY KEY,
                                  email character varying,
                                  ngay_nhan timestamp without time zone NOT NULL,
                                  ngay_tra timestamp without time zone NOT NULL,
                                  phuong_thuc_thanh_toan character varying,
                                  sdt_nguoi_dat character varying,
                                  so_nguoi_lon integer,
                                  so_phong character varying,
                                  so_tre_em integer,
                                  ten_nguoi_dat character varying,
                                  tien_coc numeric,
                                  tong_so_nguoi integer,
                                  tong_thanh_toan numeric,
                                  trang_thai character varying,
                                  id_kh integer,
                                  ghi_chu text,

                                  CONSTRAINT dat_phong_id_kh_fkey
                                      FOREIGN KEY (id_kh) REFERENCES public.khach_hang(id_kh)
);

CREATE TABLE public.chi_tiet_dat_phong (
                                           id_ct_dat_phong integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                           id_phong integer,
                                           ma_dat_phong character varying,
                                           so_luong_phong integer DEFAULT 1,

                                           CONSTRAINT chi_tiet_dat_phong_id_phong_fkey
                                               FOREIGN KEY (id_phong) REFERENCES public.phong(id_phong),

                                           CONSTRAINT chi_tiet_dat_phong_ma_dat_phong_fkey
                                               FOREIGN KEY (ma_dat_phong) REFERENCES public.dat_phong(ma_dat_phong)
);

-- =========================
-- 4. LƯU TRÚ
-- =========================

CREATE TABLE public.luu_tru (
                                id_luutru integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                ma_dat_phong character varying,
                                thoi_gian_checkin_thuc_te timestamp without time zone DEFAULT now(),
                                thoi_gian_checkout_thuc_te timestamp without time zone,
                                so_nguoi_thuc_te integer,
                                ma_book_room character varying,
                                late_fee numeric,
                                late_hours double precision,

                                CONSTRAINT luu_tru_ma_dat_phong_fkey
                                    FOREIGN KEY (ma_dat_phong) REFERENCES public.dat_phong(ma_dat_phong)
);

-- =========================
-- 5. TÀI SẢN / THIỆT HẠI
-- =========================

CREATE TABLE public.tai_san (
                                id_taisan integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                ten_tai_san character varying NOT NULL,
                                gia_tri_boi_thuong numeric,
                                id_phong integer,

                                CONSTRAINT tai_san_id_phong_fkey
                                    FOREIGN KEY (id_phong) REFERENCES public.phong(id_phong)
);

CREATE TABLE public.thiet_hai (
                                  id_thie_thai integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                  muc_do character varying,
                                  so_tien_boi_thuong numeric,
                                  trang_thai character varying,
                                  id_taisan integer,
                                  id_luutru integer,
                                  so_luong integer,

                                  CONSTRAINT thiet_hai_id_taisan_fkey
                                      FOREIGN KEY (id_taisan) REFERENCES public.tai_san(id_taisan),

                                  CONSTRAINT thiet_hai_id_luutru_fkey
                                      FOREIGN KEY (id_luutru) REFERENCES public.luu_tru(id_luutru)
);

-- =========================
-- 6. DỊCH VỤ / THANH TOÁN
-- =========================

CREATE TABLE public.su_dung_dich_vu (
                                        id_sudung_dv integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                        soluong integer DEFAULT 1,
                                        thoi_gian timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
                                        thanh_tien numeric,
                                        id_dichvu integer,
                                        id_luutru integer,
                                        id_hoadon integer,

                                        CONSTRAINT su_dung_dich_vu_id_dichvu_fkey
                                            FOREIGN KEY (id_dichvu) REFERENCES public.dich_vu(id_dichvu),

                                        CONSTRAINT su_dung_dich_vu_id_luutru_fkey
                                            FOREIGN KEY (id_luutru) REFERENCES public.luu_tru(id_luutru),

                                        CONSTRAINT su_dung_dich_vu_id_hoadon_fkey
                                            FOREIGN KEY (id_hoadon) REFERENCES public.hoa_don(id_hoadon)
);

CREATE TABLE public.thanh_toan (
                                   id_thanhtoan integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                   so_tien numeric NOT NULL,
                                   phuong_thuc character varying,
                                   trang_thai character varying,
                                   id_hoadon integer,

                                   CONSTRAINT thanh_toan_id_hoadon_fkey
                                       FOREIGN KEY (id_hoadon) REFERENCES public.hoa_don(id_hoadon)
);