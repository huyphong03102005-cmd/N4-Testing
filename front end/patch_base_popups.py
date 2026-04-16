import os
import re

dir_path = r"c:\Users\PC\Downloads\front end-20260325T094725Z-1-001\front end"
base_path = os.path.join(dir_path, "base.html")

with open(base_path, 'r', encoding='utf-8') as f:
    base_content = f.read()

# 1. Extract CSS
css_match = re.search(r'(/\* --- Popup xác nhận đăng xuất ---\s*\*/.*?)(\s*</style>)', base_content, re.DOTALL)
if css_match:
    popup_css = '\n        ' + css_match.group(1).strip()
else:
    print("Cannot find CSS in base.html")
    exit(1)

# 2. Extract HTML
html_match = re.search(r'(<!-- Popup xác nhận đăng xuất -->.*?)(<script src="shared.js">|<script>)', base_content, re.DOTALL)
if html_match:
    popup_html = '\n    ' + html_match.group(1).strip() + '\n'
else:
    print("Cannot find HTML in base.html")
    exit(1)

# 3. Extract JS
js_match = re.search(r'(function toggleLogoutPopup\(\) \{.*?)(\s*</script>)', base_content, re.DOTALL)
if js_match:
    popup_js = '\n        ' + js_match.group(1).strip() + '\n'
else:
    print("Cannot find JS in base.html")
    exit(1)

target_files = ['Tongquan.html', 'DAT_PHONG.html', 'QL_DatPhong.html', 'Service.html']

for file in target_files:
    path = os.path.join(dir_path, file)
    with open(path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # Inject CSS
    if '/* --- Popup xác nhận đăng xuất ---' not in content:
        content = re.sub(r'(</style>)', lambda m: popup_css + '\n    ' + m.group(1), content)
    
    # Inject HTML
    if '<!-- Popup xác nhận đăng xuất -->' not in content:
        content = re.sub(r'(<script src="shared.js">|<script>)', lambda m: popup_html + '\n    ' + m.group(1), content, count=1)
    
    # Inject JS
    if 'function toggleLogoutPopup()' not in content:
        # Put it in the first <script> block after HTML inject or anywhere safe
        # Find the last </script> and put before it? No, just before </body>
        script_block = f"""
    <script>
{popup_js}
    </script>
</body>"""
        content = re.sub(r'</body>', script_block, content)
    
    # Hook icons
    content = re.sub(r'<i class="([^"]*fa-user)[^"]*"[^>]*>.*?</i>', r'<i class="\1" title="Tài khoản người dùng" onclick="openUserInfo()"></i>', content)
    content = re.sub(r'<i class="([^"]*fa-arrow-right-from-bracket)[^"]*"[^>]*>.*?</i>', r'<i class="\1" title="Đăng xuất" onclick="toggleLogoutPopup()"></i>', content)
    
    with open(path, 'w', encoding='utf-8') as f:
        f.write(content)
    print(f"Patched {file}")

# For nhan_phong.html, it already has userInfoOverlay but maybe we just replace it up
np_path = os.path.join(dir_path, 'nhan_phong.html')
with open(np_path, 'r', encoding='utf-8') as f:
    np_content = f.read()

# Replace old popup HTML
if '<!-- Popup xác nhận đăng xuất' not in np_content:
    np_content = re.sub(r'<!-- Overlay Thông tin người dùng -->.*?<script src="shared.js">', lambda m: popup_html + '\n    <script src="shared.js">', np_content, flags=re.DOTALL)
else:
    # Just replace it entirely
    np_content = re.sub(r'<!-- Popup xác nhận đăng xuất -->.*?(<script src="shared.js">)', lambda m: popup_html + '\n    ' + m.group(1), np_content, flags=re.DOTALL)

# CSS is likely missing logout/pw stuff, just append to </style>
if '.logout-popup {' not in np_content:
    np_content = re.sub(r'(</style>)', lambda m: popup_css + '\n    ' + m.group(1), np_content)

# JS
if 'function toggleLogoutPopup()' not in np_content:
    script_block = f"""
    <script>
{popup_js}
    </script>
</body>"""
    np_content = re.sub(r'</body>', script_block, np_content)

np_content = re.sub(r'<i class="([^"]*fa-user)[^"]*"[^>]*>.*?</i>', r'<i class="\1" title="Tài khoản người dùng" onclick="openUserInfo()"></i>', np_content)
np_content = re.sub(r'<i class="([^"]*fa-arrow-right-from-bracket)[^"]*"[^>]*>.*?</i>', r'<i class="\1" title="Đăng xuất" onclick="toggleLogoutPopup()"></i>', np_content)

with open(np_path, 'w', encoding='utf-8') as f:
    f.write(np_content)
print("Patched nhan_phong.html")
