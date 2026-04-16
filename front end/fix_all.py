import os
import re

files = ["DAT_PHONG.html", "QL_DatPhong.html", "Service.html", "nhan_phong.html", "base.html"]

clean_popup = """
    <!-- Popup xác nh?n ðãng xu?t -->
    <div class="logout-popup" id="logoutPopup">
        <p>B?n mu?n ðãng xu?t?</p>
        <div class="logout-popup-actions">
            <button class="btn-logout-cancel" onclick="toggleLogoutPopup()">H?y</button>
            <button class="btn-logout-confirm" onclick="window.location.href='dang_nhap.html'">Xác nh?n</button>
        </div>
    </div>
"""

clean_js = """
    <script>
        function toggleLogoutPopup() {
            var popup = document.getElementById('logoutPopup');
            if (popup) popup.classList.toggle('show');
        }
        document.addEventListener('click', function (e) {
            var popup = document.getElementById('logoutPopup');
            var icon = document.querySelector('.fa-arrow-right-from-bracket');
            if (popup && icon && !popup.contains(e.target) && e.target !== icon) {
                popup.classList.remove('show');
            }
        });
    </script>
"""

for f in files:
    if not os.path.exists(f): continue
    
    with open(f, "r", encoding="utf-8") as file:
        content = file.read()
    
    # Remove all corrupted and normal logout popups
    # Matches: <!-- Popup ... Ä‘Äƒng xuáº¥t --> or <!-- Popup xác nh?n ðãng xu?t --> ... </div>
    # A bit risky to use regex without precise bounds. 
    # The logout popup starts with <div class="logout-popup" id="logoutPopup"> and ends with its closing </div>.
    
    # 1. Strip the HTML block for logout-popup
    content = re.sub(r'<!-- Popup.*?ðãng xu?t.*?-->\s*<div class="logout-popup" id="logoutPopup">.*?</div>\s*</div>', '', content, flags=re.DOTALL | re.IGNORECASE)
    content = re.sub(r'<!-- Popup.*?xuáº¥t.*?-->\s*<div class="logout-popup" id="logoutPopup">.*?</div>\s*</div>', '', content, flags=re.DOTALL | re.IGNORECASE)
    # Generic strip if comments are missing
    content = re.sub(r'<div class="logout-popup"[^>]*>.*?</div>\s*</div>', '', content, flags=re.DOTALL)
    
    # 2. Strip the toggleLogoutPopup JS logic
    content = re.sub(r'function toggleLogoutPopup\s*\(\)\s*\{[^}]*\}\s*', '', content)
    # Strip the exact event listener for logout popup
    content = re.sub(r'document\.addEventListener\(\'click\',\s*function\s*\(\s*e\s*\)\s*\{\s*(const|var|let)\s+popup\s*=\s*document\.getElementById\(\'logoutPopup\'\).*?\}\);', '', content, flags=re.DOTALL)
    
    # Also fix the weird nested empty script tags or dangling things if they exist
    content = re.sub(r'<script>\s*</script>', '', content)

    # Now, add them cleanly before </body>
    # First, find </body>
    if '</body>' in content:
        # insert the popup HTML before </body>
        content = content.replace('</body>', f"{clean_popup}\n{clean_js}\n</body>")
    else:
        content += f"{clean_popup}\n{clean_js}\n"

    with open(f, "w", encoding="utf-8") as file:
        file.write(content)
        
print("Done fixing HTML files.")
