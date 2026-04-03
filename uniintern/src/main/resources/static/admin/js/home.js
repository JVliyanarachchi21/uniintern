document.addEventListener("DOMContentLoaded", function () {
    const header = document.getElementById("siteHeader");
    const toggle = document.getElementById("mobileToggle");
    const mobileMenu = document.getElementById("mobileMenu");

    function updateHeaderState() {
        if (!header) return;

        if (window.scrollY > 20) {
            header.classList.add("scrolled");
        } else {
            header.classList.remove("scrolled");
        }
    }

    function closeMenu() {
        if (!mobileMenu || !toggle) return;

        mobileMenu.classList.remove("open");
        toggle.setAttribute("aria-expanded", "false");
    }

    updateHeaderState();
    window.addEventListener("scroll", updateHeaderState);

    if (toggle && mobileMenu) {
        toggle.addEventListener("click", function () {
            const isOpen = mobileMenu.classList.toggle("open");
            toggle.setAttribute("aria-expanded", String(isOpen));
        });

        mobileMenu.querySelectorAll("a").forEach(function (link) {
            link.addEventListener("click", closeMenu);
        });
    }
});