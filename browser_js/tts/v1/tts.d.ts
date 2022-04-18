export namespace tts {
    export const isTtsSupported: boolean;
    export const canChangeTtsVolume: boolean;
    export function createTtsOrThrow(): nl.marc_apps.tts.TextToSpeechInstanceJS;
    export function createTtsOrNull(): nl.marc_apps.tts.TextToSpeechInstanceJS | null;
    export namespace nl.marc_apps.tts {
        export class TextToSpeechInstanceJS {
            volume: number;
            isMuted: boolean;
            pitch: number;
            rate: number;
            readonly language: string;
            private constructor();
            enqueue(text: string): void;
            enqueue(text: string, clearQueue: boolean): void;
            stop(): void;
            close(): void;
        }
    }
}
