import os
import re

dir_path = r"c:\Users\PC\Downloads\front end-20260325T094725Z-1-001\front end"
base_path = os.path.join(dir_path, "base.html")

with open(base_path, 'r', encoding='utf-8') as f:
    base_content = f.read()

css_match = re.search(r'(/\* --- Popup xác nhận đăng xuất ---\s*\*/.*?)(\s*</style>)', base_content, re.DOTALL)
popup_css = css_match.group(1).strip()

# These files need CSS injected into their inline <style> block
for fname in ['Tongquan.html', 'Service.html', 'nhan_phong.html']:
    path = os.path.join(dir_path, fname)
    with open(path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    if '.logout-popup {' not in content:
        # inject CSS before </style>
        # Find last </style> before </head>
        head_match = re.search(r'</style>\s*</head>', content, re.DOTALL)
        if head_match:
            insert_pos = head_match.start()
            content = content[:insert_pos] + '\n' + popup_css + '\n' + content[insert_pos:]
            with open(path, 'w', encoding='utf-8') as f:
                f.write(content)
            print(f"Fixed CSS in {fname}")
        else:
            print(f"Could not find </style></head> in {fname}")
    else:
        print(f"{fname} already has CSS")
