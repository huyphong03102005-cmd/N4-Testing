import re
import os

files = [
    r"c:\Users\PC\OneDrive\Máy tính\LTW1\Tongquan.html",
    r"c:\Users\PC\OneDrive\Máy tính\LTW1\1.html"
]

cards_html = """            <div class="summary-wrapper">
                <div class="summary-card">
                    <div class="summary-content">
                        <h3>Tổng phòng</h3>
                        <div class="number" id="cnt-total">12</div>
                    </div>
                    <div class="summary-icon icon-orange"><i class="fa-solid fa-house"></i></div>
                </div>
                <div class="summary-card">
                    <div class="summary-content">
                        <h3>Phòng trống</h3>
                        <div class="number" id="cnt-trong">6</div>
                    </div>
                    <div class="summary-icon icon-green"><i class="fa-solid fa-door-open"></i></div>
                </div>
                <div class="summary-card">
                    <div class="summary-content">
                        <h3>Đang sử dụng</h3>
                        <div class="number" id="cnt-dangsu">3</div>
                    </div>
                    <div class="summary-icon icon-pink"><i class="fa-solid fa-bed"></i></div>
                </div>
                <div class="summary-card">
                    <div class="summary-content">
                        <h3>Đã đặt</h3>
                        <div class="number" id="cnt-dadat">2</div>
                    </div>
                    <div class="summary-icon icon-light-orange"><i class="fa-regular fa-calendar-check"></i></div>
                </div>
                <div class="summary-card">
                    <div class="summary-content">
                        <h3>Bảo trì</h3>
                        <div class="number" id="cnt-baotri">1</div>
                    </div>
                    <div class="summary-icon" style="background-color: #90caf9; color: white;"><i class="fa-solid fa-wrench"></i></div>
                </div>
            </div>"""

for filepath in files:
    if not os.path.exists(filepath):
        continue
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()

    # 1. Replace the entire <div class="summary-wrapper">...</div>
    content = re.sub(
        r'<div class="summary-wrapper">.*?</div>\s*(?:<!-- MAIN CONTENT BLOCK -->\s*)?<div class="dashboard-content">',
        cards_html + '\n\n            <div class="dashboard-content">',
        content,
        flags=re.DOTALL
    )

    # 2. Add update logic into <script>
    js_update = """
        function updateSummaryCards() {
            if (document.getElementById('cnt-total')) document.getElementById('cnt-total').innerText = document.querySelectorAll('.room').length;
            if (document.getElementById('cnt-trong')) document.getElementById('cnt-trong').innerText = document.querySelectorAll('.room.status-trong').length;
            if (document.getElementById('cnt-dangsu')) document.getElementById('cnt-dangsu').innerText = document.querySelectorAll('.room.status-dangsu-dung').length;
            if (document.getElementById('cnt-dadat')) document.getElementById('cnt-dadat').innerText = document.querySelectorAll('.room.status-dadat').length;
            if (document.getElementById('cnt-baotri')) document.getElementById('cnt-baotri').innerText = document.querySelectorAll('.room.status-baotri').length;
        }

        const roomData"""
    
    if "updateSummaryCards" not in content:
        content = content.replace("        const roomData", js_update)
        
        # update display string on success change
        content = content.replace(
            "document.getElementById('roomChangeSuccessModal').style.display = 'flex';",
            "document.getElementById('roomChangeSuccessModal').style.display = 'flex';\n                    updateSummaryCards();"
        )
        # update on init
        content = content.replace("    </script>", "        updateSummaryCards();\n    </script>")

    with open(filepath, 'w', encoding='utf-8') as f:
        f.write(content)

print("Hoan tat.")
