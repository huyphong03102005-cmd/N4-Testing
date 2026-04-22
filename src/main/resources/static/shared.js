// 1. Initial State
const defaultRoomsData = [
    { id: '101', name: '101', floor: 1, status: 'Trống', customer: '', date: '', date2: '', price: 500000, basePrice: 2500000, items: [] },
    { id: '102', name: '102', floor: 1, status: 'Đang sử dụng', customer: 'Tôn Thất Huy Phong', date: '20/08/2025', date2: '25/08/2025', price: 500000, basePrice: 1200000, items: [] },
    { id: '103', name: '103', floor: 1, status: 'Trống', customer: '', date: '', date2: '', price: 500000, basePrice: 2500000, items: [] },
    { id: '201', name: '201', floor: 2, status: 'Đã đặt', customer: 'Nguyễn Thị Ny', date: '21/08/2025', date2: '22/08/2025', price: 500000, basePrice: 1000000, items: [] },
    { id: '202', name: '202', floor: 2, status: 'Trống', customer: '', date: '', date2: '', price: 500000, basePrice: 2500000, items: [] },
    { id: '203', name: '203', floor: 2, status: 'Bảo trì', customer: '', date: '', date2: '', price: 500000, basePrice: 2500000, items: [] },
    { id: '301', name: '301', floor: 3, status: 'Trống', customer: '', date: '', date2: '', price: 500000, basePrice: 2500000, items: [] },
    { id: '302', name: '302', floor: 3, status: 'Đang sử dụng', customer: 'Trần Thanh Nhã', date: '23/05/2024', date2: '25/05/2024', price: 500000, basePrice: 3000000, items: [] },
    { id: '303', name: '303', floor: 3, status: 'Đã đặt', customer: 'Vy Minh Quân', date: '24/05/2024', date2: '26/05/2024', price: 500000, basePrice: 3000000, items: [] },
    { id: '401', name: '401', floor: 4, status: 'Đang sử dụng', customer: 'Trần Thanh Nhã', date: '01/01/2026', date2: '03/01/2026', price: 500000, basePrice: 4000000, items: [] },
    { id: '402', name: '402', floor: 4, status: 'Trống', customer: '', date: '', date2: '', price: 500000, basePrice: 2500000, items: [] },
    { id: '403', name: '403', floor: 4, status: 'Trống', customer: '', date: '', date2: '', price: 500000, basePrice: 2500000, items: [] }
];

function initSharedData() {
    if (!localStorage.getItem('sharedRoomsData')) {
        localStorage.setItem('sharedRoomsData', JSON.stringify(defaultRoomsData));
    }
}

function getSharedRooms() {
    return JSON.parse(localStorage.getItem('sharedRoomsData'));
}

function saveSharedRooms(data) {
    localStorage.setItem('sharedRoomsData', JSON.stringify(data));
    window.dispatchEvent(new Event('storageChanged'));
}

// 2. Add Global Nav Links
function setupGlobalNav() {
    const defaultNavs = document.querySelectorAll('.nav-bar .nav-item');
    const tabNavs = document.querySelectorAll('.tabs-container .tab');
    const mapping = [
        '/tongquan',
        '/dat-phong',
        '/ql-datphong',
        '/nhan-phong',
        '/tra-phong',
        '/service'
    ];

    const attachLinks = (navs) => {
        navs.forEach((nav, idx) => {
            if (mapping[idx]) {
                // Keep onclick for Service if it's already there, else overwrite href
                if (nav.tagName.toLowerCase() === 'a' && !nav.hasAttribute('onclick')) {
                    nav.href = mapping[idx];
                } else {
                    const originalClick = nav.onclick;
                    nav.onclick = function (e) {
                        if (window.location.pathname.includes('/service')) {
                            if (originalClick) originalClick.apply(this, arguments);
                            return; // let native logic run
                        }
                        window.location.href = mapping[idx];
                    }
                }
            }
        });
    };

    attachLinks(defaultNavs);
    if (!window.location.pathname.includes('/service')) {
        attachLinks(tabNavs);
    }


}

// 3. Sync Logic for Tongquan.html
function syncTongquan() {
    const rooms = getSharedRooms();
    document.querySelectorAll('.room').forEach(roomEl => {
        const num = roomEl.querySelector('.room-number');
        if (!num) return;
        const roomObj = rooms.find(r => r.id === num.innerText.trim());
        if (roomObj) {
            roomEl.className = 'room'; // reset
            let statusClass = 'status-trong';
            if (roomObj.status === 'Đang sử dụng') statusClass = 'status-dangsu-dung';
            else if (roomObj.status === 'Đã đặt') statusClass = 'status-dadat';
            else if (roomObj.status === 'Bảo trì') statusClass = 'status-baotri';
            else if (roomObj.status === 'Đã hủy') statusClass = 'status-dahuy';


            roomEl.classList.add(statusClass);
            roomEl.querySelector('.room-status').innerText = roomObj.status;

            let detailEl = roomEl.querySelector('.room-detail');
            if (roomObj.customer) {
                if (!detailEl) {
                    detailEl = document.createElement('div');
                    detailEl.className = 'room-detail';
                    roomEl.appendChild(detailEl);
                }
                detailEl.innerHTML = roomObj.customer + (roomObj.date ? '<br>→ ' + roomObj.date : '');
            } else {
                if (detailEl) detailEl.remove();
            }
        }
    });
    if (typeof updateSummaryCards === 'function') updateSummaryCards();
}

// 4. Sync Logic for Service.html
function overrideServiceData() {
    if (window.roomsData) {
        window.roomsData = getSharedRooms();
    }
}

document.addEventListener('DOMContentLoaded', () => {
    initSharedData();
    setupGlobalNav();

    const path = window.location.pathname;
    if (path.includes('/tongquan')) {
        syncTongquan();
    } else if (path.includes('/service')) {
        overrideServiceData();
        if (window.location.hash === '#checkout' && typeof switchMainTab === 'function') {
            switchMainTab('checkout');
        } else if (window.location.hash === '#service' && typeof switchMainTab === 'function') {
            switchMainTab('service');
        }
    }
});

// Helper for other pages
window.sharedData = { getSharedRooms, saveSharedRooms };
