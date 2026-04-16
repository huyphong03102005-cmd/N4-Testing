import os
import re
import glob

original_dir = r"c:\Users\PC\Downloads\front-end_Final-20260416T030729Z-3-001\front-end_Final\front end\temp_restore\front-end_Final\front end"
target_dir = r"c:\Users\PC\Downloads\front-end_Final-20260416T030729Z-3-001\front-end_Final\front end"
base_file = os.path.join(original_dir, "base.html")

# Read base.html for the popup code
with open(base_file, 'r', encoding='utf-8') as f:
    base_content = f.read()

m_icons = re.search(r'(<div class="top-icons">.*?</div>)', base_content, re.DOTALL)
top_icons_html = m_icons.group(1) if m_icons else ''

m_popups = re.search(r'(<!-- Popup xác nhận đăng xuất -->.*?)<script src="shared.js">', base_content, re.DOTALL)
popups_html = m_popups.group(1) if m_popups else ''

m_script = re.search(r'(<script>\s*function toggleLogoutPopup.*?</script>)', base_content, re.DOTALL)
popup_script = m_script.group(1) if m_script else ''

m_css = re.search(r'(/\* --- Popup xác nhận đăng xuất --- \*/.*?)</style>', base_content, re.DOTALL)
popup_css = m_css.group(1) if m_css else ''

link_mapping = {
    "Tổng quan": "tongquan.html",
    "Đặt phòng": "DAT_PHONG.html",
    "Quản lý đặt phòng": "QL_DatPhong.html",
    "Nhận phòng": "nhan_phong.html",
    "Trả phòng": "#",
    "Dịch vụ phòng": "Service.html"
}

files_to_fix = ["tongquan.html", "DAT_PHONG.html"]

for filename in files_to_fix:
    src_file = os.path.join(original_dir, filename)
    dest_file = os.path.join(target_dir, filename)
    
    with open(src_file, 'r', encoding='utf-8') as f:
        content = f.read()
    
    new_content = content
    
    # 1. Fix Background images logic
    new_content = new_content.replace("url('./Logo_bar.png')", "url('./Picture/Logo_bar.png')")
    new_content = new_content.replace("url('./Background.png')", "url('./Picture/Background.png')")

    # 2. Fix links
    for text, dest_filename in link_mapping.items():
        pattern = r'<a([^>]*)href="[^"]*"([^>]*)>(\s*' + text + r'\s*)</a>'
        new_content = re.sub(pattern, r'<a\1href="' + dest_filename + r'"\2>\3</a>', new_content)
        pattern2 = r"<a([^>]*)href='[^']*'([^>]*)>(\s*" + text + r"\s*)</a>"
        new_content = re.sub(pattern2, r'<a\1href="' + dest_filename + r'"\2>\3</a>', new_content)
        
    # 3. Inject popup logic
    new_content = re.sub(r'<div class="top-icons">.*?</div>', top_icons_html, new_content, flags=re.DOTALL)
    
    if '/* --- Popup xác nhận đăng xuất --- */' not in new_content and popup_css:
        new_content = new_content.replace('</style>', popup_css + '\n    </style>')
        
    if '<!-- Popup xác nhận đăng xuất -->' not in new_content and popups_html:
        if '<script src="shared.js">' in new_content:
            new_content = new_content.replace('<script src="shared.js">', popups_html + '\n    <script src="shared.js">')
        elif '</body>' in new_content:
            new_content = new_content.replace('</body>', popups_html + '\n</body>')
            
    if 'function toggleLogoutPopup()' not in new_content and popup_script:
        new_content = new_content.replace('</body>', popup_script + '\n</body>')

    if 'font-awesome/6.4.0/css/all.min.css' not in new_content:
        new_content = new_content.replace('</title>', '</title>\n    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">')

    # Save to dest
    with open(dest_file, 'w', encoding='utf-8') as f:
        f.write(new_content)
    print(f"Fixed and restored {filename}")

