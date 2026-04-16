import os
import re

dir_path = r"c:\Users\PC\Downloads\front end-20260325T094725Z-1-001\front end"
base_path = os.path.join(dir_path, "base.html")

with open(base_path, 'r', encoding='utf-8') as f:
    base_content = f.read()

css_match = re.search(r'(/\* --- Popup xác nhận đăng xuất ---\s*\*/.*?)(\s*</style>)', base_content, re.DOTALL)
popup_css = '\n' + css_match.group(1).strip()

# 1. Fix QL_DatPhong.css
css_file = os.path.join(dir_path, "QL_DatPhong.css")
if os.path.exists(css_file):
    with open(css_file, 'r', encoding='utf-8') as f:
        ql_css_content = f.read()
    if '/* --- Popup xác nhận đăng xuất ---' not in ql_css_content:
        with open(css_file, 'a', encoding='utf-8') as f:
            f.write('\n' + popup_css + '\n')
        print("Fixed QL_DatPhong.css")

# 2. Fix DAT_PHONG.html
dp_html = os.path.join(dir_path, "DAT_PHONG.html")
if os.path.exists(dp_html):
    with open(dp_html, 'r', encoding='utf-8') as f:
        dp = f.read()
    dp = dp.replace('<i class="fa-solid fa-user" title="Tài khoản người dùng" onclick="openUserInfo()"></i>', '<i class="fa-solid fa-user"></i>')
    with open(dp_html, 'w', encoding='utf-8') as f:
        f.write(dp)
    print("Fixed DAT_PHONG.html")
