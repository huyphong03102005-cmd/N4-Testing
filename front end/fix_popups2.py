import os
import re

dir_path = r"c:\Users\PC\Downloads\front end-20260325T094725Z-1-001\front end"
base_path = os.path.join(dir_path, "base.html")

with open(base_path, 'r', encoding='utf-8') as f:
    base_content = f.read()

css_match = re.search(r'(/\* --- Popup xác nhận đăng xuất ---\s*\*/.*?)(\s*</style>)', base_content, re.DOTALL)
popup_css = '\n' + css_match.group(1).strip() + '\n'

html_match = re.search(r'(<!-- Popup xác nhận đăng xuất -->.*?)(\s*<script src="shared.js">|\s*<script>)', base_content, re.DOTALL)
popup_html = '\n' + html_match.group(1).strip() + '\n'

js_match = re.search(r'(function toggleLogoutPopup\(\) \{.*?)(\s*</script>)', base_content, re.DOTALL)
popup_js = '\n' + js_match.group(1).strip() + '\n'

for f in ['Tongquan.html', 'DAT_PHONG.html', 'Service.html']:
    path = os.path.join(dir_path, f)
    with open(path, 'r', encoding='utf-8') as file:
        content = file.read()
    
    modified = False
    
    if 'id="logoutPopup"' not in content:
        # Inject HTML right before </body>
        # Actually it needs to be before scripts too, but let's just put it before <script src="shared.js">
        content = re.sub(r'<script src="shared.js"></script>', lambda m: popup_html + '\n' + m.group(0), content, count=1)
        modified = True
        print(f"Injected HTML into {f}")
        
    if modified:
        with open(path, 'w', encoding='utf-8') as file:
            file.write(content)
