import type { MicronautErrorResponse } from "@/types";

interface IApiResponse<T> {
    data: T;
    error: MicronautErrorResponse | null;
}

export interface SuccessResponse<T> extends IApiResponse<T> {
    data: T;
    error: null;
}

export interface ErrorResponse extends IApiResponse<null> {
    data: null;
    error: MicronautErrorResponse;
}

export type ApiResponse<T> = SuccessResponse<T> | ErrorResponse;

export async function mapApiResponse<T>(
    res: Response
): Promise<ApiResponse<T>> {
    const value = await res.json();

    if (!res.ok) {
        return { data: null, error: value } as ErrorResponse;
    }
    return { data: value, error: null } as SuccessResponse<T>;
}
