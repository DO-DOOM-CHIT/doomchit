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

    // Search Logic
    if (searchInput && searchResultBox) {
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
});
