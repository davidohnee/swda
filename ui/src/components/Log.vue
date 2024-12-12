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
