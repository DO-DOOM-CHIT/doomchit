document.addEventListener('DOMContentLoaded', () => {
    // DOM Elements
    const searchInput = document.getElementById('searchInput');
    const searchResultBox = document.getElementById('searchResultBox');

    console.log('Search Init:', searchInput, searchResultBox); // 디버깅용

    // Debounce Function
    function debounce(func, wait) {
        let timeout;
        return function (...args) {
            clearTimeout(timeout);
            timeout = setTimeout(() => func.apply(this, args), wait);
        };
    }

    let selectedIndex = -1;

    // Search Logic
    if (searchInput && searchResultBox) {
        // Keyboard Navigation
        searchInput.addEventListener('keydown', (e) => {
            const items = searchResultBox.querySelectorAll('.search-item');
            if (items.length === 0) return;

            if (e.key === 'ArrowDown') {
                e.preventDefault();
                selectedIndex++;
                if (selectedIndex >= items.length) selectedIndex = items.length - 1;
                updateSelection(items);
            } else if (e.key === 'ArrowUp') {
                e.preventDefault();
                selectedIndex--;
                if (selectedIndex < -1) selectedIndex = -1;
                updateSelection(items);
            } else if (e.key === 'Enter') {
                e.preventDefault();
                const targetIndex = selectedIndex === -1 ? 0 : selectedIndex;
                if (items[targetIndex]) items[targetIndex].click();
            }
        });

        function updateSelection(items) {
            items.forEach((item, index) => {
                if (index === selectedIndex) {
                    item.classList.add('selected');
                    item.scrollIntoView({ block: 'nearest' });
                } else {
                    item.classList.remove('selected');
                }
            });
        }
        
        const handleSearch = async (e) => {
            const keyword = e.target.value.trim();

            if (keyword.length < 1) {
                searchResultBox.classList.remove('active');
                searchResultBox.innerHTML = '';
                return;
            }
            
            try {
                const res = await fetch(`/doomchit/search?keyword=${encodeURIComponent(keyword)}`);
                const data = await res.json();
                
                renderSearchResults(data, keyword);
            } catch (err) {
                console.error('Search Error:', err);
            }
        };
        
        // 300ms Delay Debounce
        searchInput.addEventListener('input', debounce(handleSearch, 300));
        
        // Hide when clicking outside
        document.addEventListener('click', (e) => {
            if (!searchInput.contains(e.target) && !searchResultBox.contains(e.target)) {
                searchResultBox.classList.remove('active');
            }
        });
        
        // Show again if Input clicked
        searchInput.addEventListener('click', () => {
            if(searchInput.value.trim().length > 0 && searchResultBox.innerHTML !== '') {
                searchResultBox.classList.add('active');
            }
        });
    }

    function renderSearchResults(data, keyword) {
        selectedIndex = -1;
        if (!data || data.length === 0) {
            searchResultBox.classList.remove('active');
            return;
        }

        // Group by Type
        const artists = data.filter(item => item.type === 'artist');
        const songs = data.filter(item => item.type === 'song');

        let html = '';

        // Artist Section (Hidden as per user request)
        /*
        if (artists.length > 0) {
            html += `<div class="search-section">아티스트</div>`;
            artists.forEach(item => {
                html += `
                    <div class="search-item">
                        <img src="${item.image}" onerror="this.src='https://cdnimg.melon.co.kr/resource/image/web/common/noArtist.png'">
                        <div class="info">
                            <div class="name">${highlightKeyword(item.name, keyword)}</div>
                            <div class="desc">${item.detail}</div>
                        </div>
                    </div>
                `;
            });
        }
        */

        // Song Section
        if (songs.length > 0) {
            html += `<div class="search-section">곡</div>`;
            songs.forEach(item => {
                // Single Quote Escape for Inline JS
                const safeParam = (str) => encodeURIComponent(str).replace(/'/g, "%27");
                
                const link = `/doomchit/music/detail/${item.id}?title=${safeParam(item.name)}&artist=${safeParam(item.detail)}&image=${safeParam(item.image)}`;

                html += `
                    <div class="search-item" onclick="location.href='${link}'">
                        <img src="${item.image}" onerror="this.src='https://cdnimg.melon.co.kr/resource/image/web/default/noAlbum_500_160715.jpg/melon/resize/64'">
                        <div class="info">
                            <div class="name">${highlightKeyword(item.name, keyword)}</div>
                            <div class="desc">${item.detail}</div>
                        </div>
                    </div>
                `;
            });
        }

        searchResultBox.innerHTML = html;
        searchResultBox.classList.add('active');
    }

    function highlightKeyword(text, keyword) {
        const regex = new RegExp(`(${keyword})`, 'gi');
        return text.replace(regex, '<span class="highlight">$1</span>');
    }
    // Sort Logic
    const sortBtn = document.querySelector('.sort-btn');
    const sortMenu = document.querySelector('.sort-menu');
    const sortItems = document.querySelectorAll('.sort-menu li');
    const chartBody = document.getElementById('chartBody');

    if (sortBtn && sortMenu && chartBody) {
        // Toggle Menu
        sortBtn.addEventListener('click', (e) => {
            e.stopPropagation();
            const isVisible = sortMenu.style.display === 'block';
            sortMenu.style.display = isVisible ? 'none' : 'block';
        });

        // Hide when clicking outside
        document.addEventListener('click', () => {
            sortMenu.style.display = 'none';
        });

        // Sort Action
        sortItems.forEach(item => {
            item.addEventListener('click', () => {
                const key = item.getAttribute('data-sort'); // comment, like, rating
                const rows = Array.from(chartBody.querySelectorAll('tr'));

                rows.sort((a, b) => {
                    const valA = parseFloat(a.getAttribute(`data-${key}`)) || 0;
                    const valB = parseFloat(b.getAttribute(`data-${key}`)) || 0;
                    return valB - valA; // Descending
                });

                // Re-append ordered rows
                rows.forEach(row => chartBody.appendChild(row));
                
                // Update Button Text
                sortBtn.innerHTML = `${item.textContent} <i class="fa-solid fa-chevron-down"></i>`;
            });
        });
    }
});
