function iniciarApp() {
    configurarTema();
    configurarMenuLateral();
    configurarCollapseMenu();
    configurarFiltrosTabela();
    configurarFormularios();
    configurarArrasteTabela();
    configurarPainelInicio();
}

if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", iniciarApp);
} else {
    iniciarApp();
}

function configurarTema() {
    const STORAGE_KEY = "gestorDePrazo.theme";
    const root = document.documentElement;
    const salvo = localStorage.getItem(STORAGE_KEY);
    const temaInicial = (salvo === "dark" || salvo === "light") ? salvo : "light";

    root.setAttribute("data-theme", temaInicial);

    const toggleBtn = document.querySelector("[data-toggle-theme]");
    const atualizarRotulo = () => {
        if (!toggleBtn) {
            return;
        }
        const atual = root.getAttribute("data-theme") || "light";
        toggleBtn.textContent = atual === "light" ? "Tema escuro" : "Tema claro";
    };

    atualizarRotulo();

    toggleBtn?.addEventListener("click", () => {
        const atual = root.getAttribute("data-theme") || "light";
        const proximo = atual === "light" ? "dark" : "light";
        root.setAttribute("data-theme", proximo);
        localStorage.setItem(STORAGE_KEY, proximo);
        atualizarRotulo();
    });
}

function configurarMenuLateral() {
    const sidebar = document.querySelector("[data-sidebar]");
    const backdrop = document.querySelector("[data-sidebar-backdrop]");
    const openBtn = document.querySelector("[data-open-sidebar]");

    if (!sidebar) {
        return;
    }

    const fechar = () => {
        sidebar.classList.remove("is-open");
        backdrop?.classList.remove("is-open");
    };

    openBtn?.addEventListener("click", () => {
        sidebar.classList.toggle("is-open");
        backdrop?.classList.toggle("is-open");
    });

    backdrop?.addEventListener("click", fechar);
}

function configurarCollapseMenu() {
    const storageKey = "gestorDePrazo.collapse.config";

    document.querySelectorAll("[data-collapse-group]").forEach((group) => {
        const toggle = group.querySelector("[data-collapse-toggle]");
        if (!toggle) {
            return;
        }

        const rotaAtivaAbre = group.classList.contains("is-open");
        const estadoSalvo = localStorage.getItem(storageKey);

        if (!rotaAtivaAbre && estadoSalvo !== null) {
            group.classList.toggle("is-open", estadoSalvo === "true");
        }

        toggle.setAttribute("aria-expanded", String(group.classList.contains("is-open")));

        toggle.addEventListener("click", () => {
            const vaiAbrir = !group.classList.contains("is-open");
            group.classList.toggle("is-open", vaiAbrir);
            toggle.setAttribute("aria-expanded", String(vaiAbrir));
            localStorage.setItem(storageKey, String(vaiAbrir));
        });
    });
}

function configurarFiltrosTabela() {
    const searchInput = document.querySelector("[data-table-search]");
    const statusFilter = document.querySelector("[data-table-status-filter]");
    const countLabel = document.querySelector("[data-table-count]");
    const tableSelector = searchInput?.dataset.tableSearch || statusFilter?.dataset.tableStatusFilter;
    const table = tableSelector ? document.querySelector(tableSelector) : null;
    const rows = table ? Array.from(table.querySelectorAll("tbody tr")) : [];

    if (!rows.length) {
        return;
    }

    function atualizar() {
        const termo = (searchInput?.value || "").trim().toLowerCase();
        const statusSelecionado = statusFilter?.value || "TODOS";
        let visiveis = 0;

        rows.forEach((row) => {
            const textoLinha = row.dataset.search || row.textContent.toLowerCase();
            const statusLinha = row.dataset.status || "";

            const bateBusca = !termo || textoLinha.includes(termo);
            const bateStatus = statusSelecionado === "TODOS" || statusLinha === statusSelecionado;

            const mostrar = bateBusca && bateStatus;
            row.style.display = mostrar ? "" : "none";
            if (mostrar) {
                visiveis++;
            }
        });

        if (countLabel) {
            const unidade = countLabel.dataset.unit || "item(ns)";
            countLabel.textContent = `Exibindo ${visiveis} de ${rows.length} ${unidade}`;
        }
    }

    searchInput?.addEventListener("input", atualizar);
    statusFilter?.addEventListener("change", atualizar);
    atualizar();
}

function configurarFormularios() {
    document.querySelectorAll("form").forEach((form) => {
        form.addEventListener("submit", (event) => {
            const mensagemConfirmacao = form.dataset.confirm;
            if (mensagemConfirmacao && !window.confirm(mensagemConfirmacao)) {
                event.preventDefault();
                return;
            }

            const submitBtn = form.querySelector('button[type="submit"]');
            if (submitBtn) {
                submitBtn.disabled = true;
                submitBtn.textContent = form.dataset.loadingText || "Salvando...";
            }
        });
    });
}

function configurarArrasteTabela() {
    document.querySelectorAll(".table-wrapper").forEach((wrapper) => {
        let isMouseDown = false;
        let startX = 0;
        let scrollLeftStart = 0;

        wrapper.addEventListener("mousedown", (e) => {
            if (e.target.closest("button, select, input, textarea, a, form")) {
                return;
            }
            isMouseDown = true;
            wrapper.classList.add("is-dragging");
            startX = e.pageX - wrapper.offsetLeft;
            scrollLeftStart = wrapper.scrollLeft;
        });

        window.addEventListener("mouseup", () => {
            isMouseDown = false;
            wrapper.classList.remove("is-dragging");
        });

        wrapper.addEventListener("mousemove", (e) => {
            if (!isMouseDown) {
                return;
            }
            e.preventDefault();
            const x = e.pageX - wrapper.offsetLeft;
            const walk = (x - startX) * 1.2;
            wrapper.scrollLeft = scrollLeftStart - walk;
        });
    });
}

function configurarPainelInicio() {
    const rows = Array.from(document.querySelectorAll("[data-doc-row]"));
    const searchInput = document.querySelector("#filtroBuscaHome");
    const statusSelect = document.querySelector("#filtroStatusHome");
    const countLabel = document.querySelector("#contadorDocumentosHome");

    if (rows.length) {
        function atualizarPainel() {
            const termo = (searchInput?.value || "").trim().toLowerCase();
            const filtro = statusSelect?.value || "PENDENTES_ATIVOS";
            let visiveis = 0;

            rows.forEach((row) => {
                const texto = row.dataset.search || row.textContent.toLowerCase();
                const situacao = row.dataset.situacao || "";
                const statusRegistro = row.dataset.statusRegistro || "";
                const encerrado = statusRegistro === "CONCLUIDO" || statusRegistro === "CANCELADO";

                const bateBusca = !termo || texto.includes(termo);
                let bateFiltro = false;

                if (filtro === "PENDENTES_ATIVOS") {
                    bateFiltro = !encerrado;
                } else if (filtro === "TODOS") {
                    bateFiltro = true;
                } else {
                    bateFiltro = situacao === filtro || statusRegistro === filtro;
                }

                const mostrar = bateBusca && bateFiltro;
                row.style.display = mostrar ? "" : "none";
                if (mostrar) {
                    visiveis++;
                }
            });

            document.querySelectorAll("[data-categoria-section]").forEach((sec) => {
                const secRows = Array.from(sec.querySelectorAll("[data-doc-row]"));
                if (!secRows.length) {
                    return;
                }
                const temVisivel = secRows.some((r) => r.style.display !== "none");
                sec.style.display = temVisivel ? "" : "none";
            });

            if (countLabel) {
                countLabel.textContent = `Exibindo ${visiveis} de ${rows.length} documento(s)`;
            }
        }

        searchInput?.addEventListener("input", atualizarPainel);
        statusSelect?.addEventListener("change", atualizarPainel);
        atualizarPainel();
    }

    document.querySelectorAll("[data-duplicar-form]").forEach((form) => {
        const triggerBtn = form.querySelector("[data-duplicar-trigger]");
        const inputData = form.querySelector("[data-duplicar-data]");

        if (!triggerBtn || !inputData) {
            return;
        }

        triggerBtn.addEventListener("click", () => {
            const estaVisivel = inputData.style.display !== "none";
            if (!estaVisivel) {
                inputData.style.display = "inline-block";
                inputData.required = true;
                inputData.focus();
                triggerBtn.textContent = "Confirmar cópia";
                return;
            }

            if (!inputData.value) {
                window.alert("Informe uma nova data de vencimento para duplicar o documento.");
                inputData.focus();
                return;
            }

            form.requestSubmit();
        });

        form.addEventListener("submit", (event) => {
            if (!inputData.value) {
                event.preventDefault();
                inputData.style.display = "inline-block";
                inputData.required = true;
                triggerBtn.textContent = "Confirmar cópia";
                window.alert("Informe uma nova data de vencimento para duplicar o documento.");
                inputData.focus();
            }
        });
    });
}