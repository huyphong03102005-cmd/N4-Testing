const fs = require('fs');

let content = fs.readFileSync('Service.html', 'utf8');

// 1. Nav bar
content = content.replace(
    `<a href="#" class="nav-item">Trả phòng</a>\n                <a href="#" class="nav-item active">Dịch vụ phòng</a>`,
    `<a href="#" class="nav-item" id="navCheckout" onclick="switchMainTab('checkout')">Trả phòng</a>\n                <a href="#" class="nav-item active" id="navService" onclick="switchMainTab('service')">Dịch vụ phòng</a>`
);

// 2. Add checkout body right after layout-grid
content = content.replace(
    `            <div class="layout-grid">`,
    `            <!-- VIEW: DỊCH VỤ -->\n            <div class="layout-grid" id="serviceView">`
);

content = content.replace(
    `                    </div>\n                </div>\n            </div>\n        </main>`,
    `                    </div>
                </div>
            </div>

            <!-- VIEW: TRẢ PHÒNG -->
            <div class="layout-grid" id="checkoutView" style="display: none;">
                <!-- Trả Phòng - Bản đồ -->
                <div id="coMapSection" class="map-section" style="width: 100%; border-right: none; padding-right: 0;">
                    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 30px; flex-shrink: 0; max-width: 100%;">
                        <div class="map-section-title" style="margin-bottom: 0;">Sơ đồ phòng (Đang sử dụng)</div>
                        <div class="search-room-wrapper" style="max-width: 400px;">
                            <i class="fa-solid fa-magnifying-glass"></i>
                            <input type="text" id="coSearchInput" placeholder="Tìm kiếm phòng..." onkeyup="searchCheckoutMap()">
                        </div>
                    </div>
                    <div id="coMapContainer" style="display: flex; flex-direction: column;"></div>
                </div>

                <!-- Trả Phòng - Chi tiết thanh toán -->
                <div id="coDetailSection" class="detail-section" style="display: none; width: 100%;">
                    <div class="btn-back-wrapper">
                        <button class="btn-back" onclick="selectCheckoutRoom(null)">
                            <i class="fa-solid fa-arrow-left"></i> Quay lại Sơ đồ
                        </button>
                    </div>
                    <div style="display: flex; gap: 30px; flex: 1;">
                        <div style="width: 60%; display: flex; flex-direction: column; overflow-y: auto; padding-right: 15px;" id="coLeftPanel"></div>
                        <div style="width: 40%; display: flex; flex-direction: column;" id="coRightPanel"></div>
                    </div>
                </div>
            </div>
        </main>`
);

// 3. Add invoice modals right before script
content = content.replace(
    `    <script>`,
    `    <!-- Popup: Hoá Đơn Thanh Toán -->
    <div class="overlay" id="invoicePopup">
        <div style="background: #fffcf5; padding: 40px; border-radius: 12px; width: 500px; box-shadow: 0 10px 25px rgba(0,0,0,0.1); border: 2px solid #eaddcf; position: relative;">
            <div style="text-align: center; margin-bottom: 20px;">
                <img src="./Picture/Logo_bar.png" alt="Logo" style="width: 50px; height: 50px;">
                <h2 style="color: #d35400;">CamFusion House</h2>
            </div>
            
            <h3 style="color: #d35400; border-bottom: 2px solid #eaddcf; padding-bottom: 8px; margin-bottom: 15px;">Thông tin khách hàng</h3>
            <div style="display: flex; justify-content: space-between; margin-bottom: 25px; font-size: 14px;">
                <div><strong>Tên khách</strong><br><span id="invCust"></span></div>
                <div><strong>Check-in</strong><br><span id="invIn"></span></div>
                <div><strong>Check-out</strong><br><span id="invOut"></span></div>
            </div>

            <h3 style="color: #d35400; border-bottom: 2px solid #eaddcf; padding-bottom: 8px; margin-bottom: 15px;">Dịch vụ phòng</h3>
            <table style="width: 100%; border-collapse: collapse; margin-bottom: 20px; font-size: 14px; text-align: left;">
                <thead><tr><th>Tên dịch vụ</th><th style="text-align:center;">Số lượng</th><th style="text-align:right;">Giá</th></tr></thead>
                <tbody id="invItemsBody"></tbody>
            </table>

            <div style="border-top: 2px solid #eaddcf; margin-bottom: 15px;"></div>

            <div style="display: flex; justify-content: space-between; font-weight: bold; margin-bottom: 10px; font-size: 15px; color:#333;">
                <span>Tiền phòng</span> <span id="invRoom"></span>
            </div>
            <div style="display: flex; justify-content: space-between; font-weight: bold; margin-bottom: 15px; font-size: 15px; color:#333;">
                <span>Dịch vụ</span> <span id="invSrv"></span>
            </div>
            <div style="display: flex; justify-content: space-between; font-weight: bold; font-size: 18px; color: #d35400; background: #eaddcf; padding: 12px; border-radius: 4px;">
                <span>Tổng tiền</span> <span id="invTotal"></span>
            </div>

            <div style="display: flex; justify-content: space-between; margin-top: 30px; gap: 10px;">
                <button class="f-btn f-btn-cancel" style="padding: 10px 20px;" onclick="closeInvoice()">HUỶ</button>
                <button class="f-btn f-btn-cancel" style="padding: 10px 20px;" onclick="exportInvoice()">XUẤT HOÁ ĐƠN ĐỎ</button>
                <button class="f-btn f-btn-save" style="padding: 10px 30px;" onclick="printInvoice()">IN HOÁ ĐƠN</button>
            </div>
        </div>
    </div>

    <!-- Popup: In Thành Công -->
    <div class="overlay" id="printSuccessPopup">
        <div class="popup" style="width: 350px;">
            <div class="popup-icon" style="font-size: 60px; color: #2ecc71; margin-bottom: 15px;"><i class="fa-regular fa-circle-check"></i></div>
            <div class="popup-title" style="font-size: 20px; margin-bottom: 25px;">In hóa đơn thành công!</div>
            <button class="popup-btn" style="background: #e74c3c; width: 120px;" onclick="closePrintSuccess()">Xong</button>
        </div>
    </div>

    <script>`
);

// 4. JS Logic for Checkout at bottom of script
content = content.replace(
    `        // Init App \n        switchTab('service');\n        renderMap();`,
    `        // ======================= TRẢ PHÒNG (CHECKOUT) LOGIC =======================
        let selectedCheckoutRoomId = null;

        function switchMainTab(tab) {
            if (tab === 'service') {
                document.getElementById('navService').classList.add('active');
                document.getElementById('navCheckout').classList.remove('active');
                document.getElementById('serviceView').style.display = 'flex';
                document.getElementById('checkoutView').style.display = 'none';
                selectRoom(null); // Quay lại sơ đồ map của dịch vụ
            } else if (tab === 'checkout') {
                document.getElementById('navCheckout').classList.add('active');
                document.getElementById('navService').classList.remove('active');
                document.getElementById('checkoutView').style.display = 'flex';
                document.getElementById('serviceView').style.display = 'none';
                selectCheckoutRoom(null); // Thiết lập sơ đồ map checkout
            }
        }

        function searchCheckoutMap() {
            const query = document.getElementById('coSearchInput').value.toLowerCase();
            const floors = document.getElementById('coMapContainer').querySelectorAll('.floor-group');
            floors.forEach(f => {
                let hasVisible = false;
                f.querySelectorAll('.map-room-card').forEach(c => {
                    if (c.querySelector('.map-room-name').innerText.toLowerCase().includes(query)) {
                        c.style.display = 'flex';
                        hasVisible = true;
                    } else c.style.display = 'none';
                });
                f.style.display = hasVisible ? 'block' : 'none';
            });
        }

        function renderCheckoutMap() {
            const container = document.getElementById('coMapContainer');
            let html = '';
            const floors = [1, 2, 3, 4, 5];
            floors.forEach(floor => {
                const floorRooms = roomsData.filter(r => r.floor === floor && r.status === 'Đang sử dụng');
                if(floorRooms.length > 0) {
                    html += \`<div class="floor-group"><div class="floor-title">Tầng \${floor}</div><div class="floor-grid">\`;
                    floorRooms.forEach(room => {
                        html += \`
                            <div class="map-room-card bg-sudung" onclick="selectCheckoutRoom('\${room.id}')">
                                <div class="map-room-name">\${room.name}</div>
                                <div class="map-room-status">\${room.status}</div>
                                <div class="map-room-customer">\${room.customer}</div>
                                <div class="map-room-date">→ \${room.date ? room.date.split(',')[0] : ''}</div>
                            </div>\`;
                    });
                    html += \`</div></div>\`;
                }
            });
            container.innerHTML = html;
            document.getElementById('coSearchInput').value = '';
        }

        function selectCheckoutRoom(id) {
            selectedCheckoutRoomId = id;
            if(!id) {
                document.getElementById('coMapSection').style.display = 'flex';
                document.getElementById('coDetailSection').style.display = 'none';
                renderCheckoutMap();
            } else {
                document.getElementById('coMapSection').style.display = 'none';
                document.getElementById('coDetailSection').style.display = 'flex';
                renderCheckoutParams();
            }
        }

        function renderCheckoutParams() {
            const lPanel = document.getElementById('coLeftPanel');
            const rPanel = document.getElementById('coRightPanel');
            const room = roomsData.find(r => r.id === selectedCheckoutRoomId);
            if(!room) return;

            // Render Left Panel
            let svHtml = '';
            let cpHtml = '';
            let svTotal = 0;
            let svIdx = 1, cpIdx = 1;

            room.items.forEach(item => {
                const total = item.price * item.qty;
                svTotal += total;
                const row = \`
                    <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:12px; font-size:15px; padding-left:15px;">
                        <span style="flex:1;">\${item.type==='service'?svIdx:cpIdx}. \${item.type==='compensation'?'Tài sản bồi thường: ':''}\${item.name}</span>
                        <div style="background:#f0f0f0; padding:2px 10px; border-radius:12px; font-size:13px; color:#555; margin-right:20px;">- \${item.qty} +</div>
                        <div style="width:100px; text-align:right;">\${formatMoney(total)}</div>
                        <i class="fa-regular fa-trash-can" style="color:#aaa; margin-left:15px; cursor:pointer;"></i>
                    </div>\`;
                if(item.type === 'service') { svHtml += row; svIdx++; }
                else { cpHtml += row; cpIdx++; }
            });

            lPanel.innerHTML = \`
                <div class="room-info-card" style="box-shadow: 0 4px 6px rgba(0,0,0,0.05); border: 1px solid #ddd; flex-shrink: 0;">
                    <div class="room-title"><i class="fa-regular fa-calendar-check" style="font-size:24px; color:#dcd1c6;"></i> Phòng \${room.name}</div>
                    <div class="room-status">Trạng thái: <span class="status-badge bg-sudung">\${room.status}</span></div>
                    <div class="room-time">
                        <span style="font-weight:600; color:#333;">Check-in:</span> <span style="background:#f4f4f4; padding:2px 8px; border-radius:4px;">\${room.date||''} <i class="fa-regular fa-calendar"></i></span>
                        <span style="font-weight:600; color:#333; margin-left:20px;">Check-out:</span> <span style="background:#f4f4f4; padding:2px 8px; border-radius:4px;">\${room.date2||''} <i class="fa-regular fa-calendar"></i></span>
                    </div>
                    <div class="room-time" style="margin-bottom: 20px;">Số người: 2</div>
                    <div class="room-total" style="border-top:1px solid #ddd; padding-top:15px; color:#666;">
                        Tiền phòng: <span style="font-weight:700; color:#333; border-bottom:1px solid #ccc; padding-bottom:2px;">\${formatMoney(room.basePrice)}</span>
                    </div>
                </div>

                <div class="room-info-card" style="box-shadow: 0 4px 6px rgba(0,0,0,0.05); border: 1px solid #ddd; flex-shrink: 0;">
                    <div style="display:flex; justify-content:space-between; margin-bottom:15px;">
                        <span style="font-size:18px; font-weight:bold;">A. Dịch vụ phòng</span>
                        <button style="background:#dcd1c6; border:none; border-radius:4px; padding:4px 10px; font-weight:600; cursor:pointer;" onclick="switchMainTab('service'); selectRoom('\${room.id}');">+ Thêm dịch vụ</button>
                    </div>
                    \${svHtml || '<i style="color:#999; font-size:14px; padding-left:15px;">Không có dịch vụ</i>'}
                    
                    <div style="display:flex; justify-content:space-between; margin-bottom:15px; margin-top:25px;">
                        <span style="font-size:18px; font-weight:bold;">B. Bồi thường thiệt hại</span>
                        <button style="background:#dcd1c6; border:none; border-radius:4px; padding:4px 10px; font-weight:600; cursor:pointer;" onclick="switchMainTab('service'); selectRoom('\${room.id}'); switchTab('compensation');">+ Thêm tài sản</button>
                    </div>
                    \${cpHtml || '<i style="color:#999; font-size:14px; padding-left:15px;">Không có bồi thường</i>'}
                </div>
            \`;

            // Render Right Panel
            rPanel.innerHTML = \`
                <div class="room-info-card" style="box-shadow: 0 4px 6px rgba(0,0,0,0.05); border: 1px solid #ddd; padding: 30px; flex-shrink: 0;">
                    <div style="display:flex; justify-content:space-between; font-size:16px; margin-bottom:25px; color:#333;">
                        <span>Tiền phòng:</span><span style="border-bottom:1px solid #ddd; padding-bottom:3px;">\${formatMoney(room.basePrice)}</span>
                    </div>
                    <div style="display:flex; justify-content:space-between; font-size:16px; margin-bottom:25px; color:#333;">
                        <span>Tổng dịch vụ phòng:</span><span style="border-bottom:1px solid #ddd; padding-bottom:3px;">\${formatMoney(svTotal)}</span>
                    </div>
                    <div style="display:flex; justify-content:space-between; font-size:16px; margin-bottom:25px; color:#333;">
                        <span>Phụ thu:</span><span style="border-bottom:1px solid #ddd; padding-bottom:3px;">0</span>
                    </div>
                    <div style="display:flex; justify-content:space-between; font-size:18px; font-weight:bold; margin-bottom:25px; color:#333;">
                        <span>TỔNG TIỀN</span><span style="border-bottom:2px solid #ccc; padding-bottom:3px;">\${formatMoney(room.basePrice + svTotal)}</span>
                    </div>
                    <hr style="border:none; border-top:1px dashed #ccc; margin: 30px 0;">
                    
                    <div style="font-weight:bold; font-size:16px; margin-bottom:20px; text-transform:uppercase;">Phương thức thanh toán</div>
                    <label style="display:flex; justify-content:space-between; font-size:15px; margin-bottom:15px; cursor:pointer;">
                        Thanh toán bằng tiền mặt
                        <input type="checkbox" id="cbCash" onchange="toggleCoPayment('cash')" style="width:18px; height:18px; accent-color:#555;">
                    </label>
                    <label style="display:flex; justify-content:space-between; font-size:15px; margin-bottom:15px; cursor:pointer;">
                        Thanh toán bằng QR
                        <input type="checkbox" id="cbQr" onchange="toggleCoPayment('qr')" style="width:18px; height:18px; accent-color:#555;">
                    </label>

                    <button class="f-btn-save" style="width:100%; border:none; padding:15px; border-radius:8px; font-weight:bold; font-size:16px; margin-top:20px; box-shadow:0 4px 8px rgba(0,0,0,0.1); cursor:pointer;" onclick="openInvoiceForm()">THANH TOÁN</button>
                </div>
            \`;
        }

        function toggleCoPayment(opt) {
            if(opt === 'cash') {
                if(document.getElementById('cbCash').checked) document.getElementById('cbQr').checked = false;
            } else {
                if(document.getElementById('cbQr').checked) document.getElementById('cbCash').checked = false;
            }
        }

        function openInvoiceForm() {
            const cbCash = document.getElementById('cbCash').checked;
            const cbQr = document.getElementById('cbQr').checked;
            if(!cbCash && !cbQr) { alert('Vui lòng chọn 1 phương thức thanh toán!'); return; }
            
            const room = roomsData.find(r => r.id === selectedCheckoutRoomId);
            document.getElementById('invCust').innerText = room.customer || '';
            document.getElementById('invIn').innerText = room.date ? room.date.split(',')[0] : '';
            document.getElementById('invOut').innerText = room.date2 ? room.date2.split(',')[0] : '';

            let tbody = '';
            let svTotal = 0;
            room.items.forEach(item => {
                const total = item.price * item.qty;
                svTotal += total;
                tbody += \`<tr>
                    <td style="padding:8px 0; border-bottom: 1px solid #eee;">\${item.type==='compensation'?'Tài sản bồi thường: ':''}\${item.name}</td>
                    <td style="text-align:center; padding:8px 0; border-bottom: 1px solid #eee;">0\${item.qty}</td>
                    <td style="text-align:right; padding:8px 0; border-bottom: 1px solid #eee;">\${formatMoney(total)}</td>
                </tr>\`;
            });
            document.getElementById('invItemsBody').innerHTML = tbody;
            document.getElementById('invRoom').innerText = formatMoney(room.basePrice);
            document.getElementById('invSrv').innerText = formatMoney(svTotal);
            document.getElementById('invTotal').innerText = formatMoney(room.basePrice + svTotal);

            document.getElementById('invoicePopup').style.display = 'flex';
        }

        function closeInvoice() { document.getElementById('invoicePopup').style.display = 'none'; }
        
        function printInvoice() {
            document.getElementById('invoicePopup').style.display = 'none';
            document.getElementById('printSuccessPopup').style.display = 'flex';
        }

        function exportInvoice() { closeInvoice(); resetCheckoutRoom(); }
        function closePrintSuccess() { document.getElementById('printSuccessPopup').style.display = 'none'; resetCheckoutRoom(); }
        
        function resetCheckoutRoom() {
            const room = roomsData.find(r => r.id === selectedCheckoutRoomId);
            if(room) { room.status = 'Trống'; room.customer = ''; room.items = []; }
            selectCheckoutRoom(null);
        }

        // Init App 
        switchTab('service'); // old init
        switchMainTab('service');
        renderMap();`
);

fs.writeFileSync('Service.html', content);
console.log('Successfully patched Service.html with checkout logic');
