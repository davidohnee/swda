<script setup lang="ts">
    import Logo from "@/assets/logo.svg";
</script>
<template>
    <div class="navigator">
        <div class="app-title">
            <img :src="Logo" />
            <h2>SWDA</h2>
        </div>

        <div class="links">
            <RouterLink to="/orders">
                <div class="entry">
                    <span class="material-symbols-rounded">shopping_cart</span>
                    <span class="title">Orders</span>
                </div>
            </RouterLink>
            <RouterLink to="/inventory">
                <div class="entry">
                    <span class="material-symbols-rounded">inventory_2</span>
                    <span class="title">Inventory</span>
                </div>
            </RouterLink>
            <RouterLink to="/customers">
                <div class="entry">
                    <span class="material-symbols-rounded">people</span>
                    <span class="title">Customers</span>
                </div>
            </RouterLink>
            <RouterLink to="/replenishments">
                <div class="entry">
                    <span class="material-symbols-rounded">local_shipping</span>
                    <span class="title">Replenishments</span>
                </div>
            </RouterLink>
        </div>
    </div>
</template>
<style scoped>
    div.navigator {
        position: sticky;
        top: 0;
        margin: 1em;
        margin-right: 0;

        /* no idea why it needs to be 4.1, instead of 4 */
        height: calc(100svh - 4em);

        padding: 1em;
        background: var(--bg);
        border: 1px solid var(--border);
        width: 220px;
        border-radius: 0.5em;
        overflow: clip;
        display: flex;
        flex-direction: column;
        justify-content: space-between;
        gap: 1em;
    }

    .app-title {
        display: grid;
        align-items: center;
        grid-template-columns: 50px 1fr;
        gap: 1em;
        margin-bottom: 1em;
        padding-bottom: 1em;

        & h2 {
            margin: 0;
        }
    }

    .links {
        display: flex;
        flex-direction: column;

        & a {
            container-type: inline-size;
            container-name: sidebar-element;

            .entry {
                display: flex;
                align-items: center;
                gap: 0.5em;
                padding: 0.5em 1em;
                border-radius: 0.5em;
                transition: background 0.2s;
                color: var(--fg);
            }

            &.router-link-active .entry {
                color: var(--accent);
                background: var(--bg-mute);
            }

            &:hover .entry {
                background: var(--bg-mute);
            }
        }
    }

    @container sidebar-element (max-width: 150px) {
        .title {
            display: none;
        }

        .entry {
            padding: 0.5em !important;
            justify-content: center;
        }
    }

    @media screen and (max-width: 750px) {
        div.navigator {
            max-width: 50px;
        }
    }
</style>
