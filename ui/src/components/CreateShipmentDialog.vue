<script setup lang="ts">
    import { ref } from "vue";
    import { api } from "@/api";
    import type { ShipmentCreate } from "@/types";

    const dialog = ref<HTMLDialogElement>();

    const departureDatetime = ref<string>("");
    const estimatedArrivalDatetime = ref<string>("");
    const orderId = ref<string>("");

    const emit = defineEmits<{
        (e: "close"): void;
    }>();

    const open = (id: string) => {
        orderId.value = id;
        departureDatetime.value = "";
        estimatedArrivalDatetime.value = "";
        dialog.value?.showModal();
    };

    const close = () => {
        dialog.value?.close();
        emit("close");
    };

    defineExpose({ open });

    const apply = async () => {
        if (!departureDatetime.value || !estimatedArrivalDatetime.value) {
            alert(
                "Please fill in both the departure date and the estimated arrival date."
            );
            return;
        }

        const departure = new Date(departureDatetime.value);
        const arrival = new Date(estimatedArrivalDatetime.value);

        if (departure > arrival) {
            alert("Departure date cannot be after estimated arrival date.");
            return;
        }

        const departureISO = departure.toISOString();
        const estimatedArrivalISO = arrival.toISOString();

        const shipmentData: ShipmentCreate = {
            orderId: orderId.value,
            departure: departureISO,
            estimatedArrival: estimatedArrivalISO,
        };

        try {
            await api.shipments.create(shipmentData);
            close();
        } catch (error) {
            console.error("Failed to create shipment:", error);
            alert(
                "An error occurred while creating the shipment. Please try again."
            );
        }
    };
</script>

<template>
    <dialog ref="dialog">
        <span
            class="material-symbols-rounded close"
            @click="close"
            aria-label="Close dialog"
        >
            close
        </span>

        <h1>Create Shipment</h1>

        <div class="form-section">
            <div class="form-group">
                <label for="departureDatetime">Departure</label>
                <input
                    type="datetime-local"
                    id="departureDatetime"
                    v-model="departureDatetime"
                    required
                />
            </div>
            <div class="form-group">
                <label for="estimatedArrivalDatetime">Estimated Arrival</label>
                <input
                    type="datetime-local"
                    id="estimatedArrivalDatetime"
                    v-model="estimatedArrivalDatetime"
                    required
                />
            </div>
        </div>

        <div class="actions">
            <button
                class="error"
                @click="close"
                type="button"
            >
                <span class="material-symbols-rounded">close</span>
                Cancel
            </button>
            <button
                class="success"
                @click="apply"
                type="button"
            >
                <span class="material-symbols-rounded">check</span>
                Ship it!
            </button>
        </div>
    </dialog>
</template>

<style scoped>
    dialog[open] {
        max-width: 500px;
        width: 90%;
        display: none;
        position: relative;
        transition: transform 0.3s ease, opacity 0.3s ease;
    }

    dialog[open] {
        display: flex;
        flex-direction: column;
        gap: 1.5em;
    }

    .form-section {
        display: flex;
        flex-direction: column;
        gap: 1em;
    }

    .form-group {
        display: flex;
        flex-direction: column;
    }

    label {
        margin-bottom: 0.5em;
        font-weight: 600;
    }

    input[type="datetime-local"] {
        padding: 0.75em;
        border: 1px solid var(--border);
        border-radius: 0.5em;
        font-size: 1rem;
        transition: border-color 0.3s, box-shadow 0.3s;
    }

    input[type="datetime-local"]:focus {
        border-color: var(--primary);
        box-shadow: 0 0 0 3px rgba(0, 123, 255, 0.25);
        outline: none;
    }

    .actions {
        display: flex;
        justify-content: flex-end;
        gap: 1em;
    }

    @media (max-width: 600px) {
        dialog {
            max-width: 90vw;
            padding: 1.5em;
        }

        h1 {
            font-size: 1.5rem;
        }

        button.cancel,
        button.save {
            padding: 0.5em 1em;
            font-size: 0.9rem;
        }
    }
</style>
