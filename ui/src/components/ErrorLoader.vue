<script setup lang="ts">
    import type { ApiResponse } from "@/api/helper";
    import type { MicronautErrorResponse } from "@/types";
    import { computed, type PropType } from "vue";

    const props = defineProps({
        content: {
            type: Object as PropType<ApiResponse<any>>,
            required: false,
        },
    });

    const isErrorResponse = computed(() => {
        return props.content?.error != null;
    });

    const castedError = computed(() => {
        return props.content?.error as MicronautErrorResponse;
    });
</script>

<template>
    <div
        class="loader"
        v-if="content === undefined"
    >
        <table>
            <thead>
                <tr aria-hidden="true">
                    <th class="skeleton skeleton-text skeleton-text__body"></th>
                </tr>
            </thead>
            <tbody>
                <tr
                    v-for="i in 5"
                    :key="i"
                >
                    <td class="skeleton skeleton-text skeleton-text__body"></td>
                </tr>
            </tbody>
        </table>
    </div>
    <div
        class="error"
        v-else-if="isErrorResponse"
    >
        <h2 class="title">
            <span class="material-symbols-rounded">error</span>
            Error
        </h2>
        <p class="message">{{ castedError.message }}</p>
        <pre class="description">{{
            castedError._embedded.errors.map((x) => x.message).join(", ")
        }}</pre>
    </div>
    <slot v-else></slot>
</template>

<style scoped>
    .error {
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        height: 60vh;

        & h2 {
            display: flex;
            align-items: center;
            gap: 0.5rem;
            color: var(--red);
            font-weight: 900;
            font-size: 2rem;
            margin: 0;

            & .material-symbols-rounded {
                font-variation-settings: "wght" 900;
                font-size: 2rem;
            }
        }

        & p {
            margin: 0;
            font-size: 1.2rem;
            font-weight: bold;
        }
    }

    .skeleton {
        background-image: linear-gradient(
            90deg,
            var(--bg-mute) 0px,
            var(--border) 100px,
            var(--bg-mute) 200px
        );
        background-size: 1200px;
        animation: shine-lines 2s infinite;
    }

    @keyframes shine-lines {
        0% {
            background-position: -100px;
        }
        60%,
        100% {
            background-position: 1000px;
        }
    }

    .skeleton-text {
        width: 100%;
        height: 0.7rem;
        border-radius: 0.25rem;
    }

    tr {
        display: block;
        padding: 0.5em;
        width: calc(100% - 2em);

        & td,
        th {
            display: block;
            width: 100%;
            margin: 0;
        }
    }
</style>
