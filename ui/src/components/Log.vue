<script setup lang="ts">
    import type { Log } from "@/types";

    defineProps({
        log: {
            type: Object as () => Log,
            required: true,
        },
    });
</script>
<template>
    <div class="log">
        <code class="datetime">
            {{ log.timestamp.toISOString() }}
        </code>
        <div
            class="level"
            :class="log.level"
        >
            {{ log.level }}
        </div>
        <div class="message">
            {{ log.message }}
        </div>
    </div>
</template>
<style scoped>
    .log {
        display: grid;
        grid-template-columns: 25ch 7ch 1fr;
        gap: 0.5rem;
        align-items: center;
        font-size: 1rem;

        &:not(:last-child) {
            border-bottom: 1px solid #ccc;
        }
    }

    code {
        font-size: 1rem;
    }

    .level.INFO {
        color: var(--accent);
    }

    .level.WARNING {
        color: var(--yellow);
    }

    .level.ERROR {
        color: var(--red);
    }
</style>
