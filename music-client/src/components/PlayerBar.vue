<template>
  <div
    v-if="song"
    style="position:fixed;left:0;right:0;bottom:0;background:#fffaf1;box-shadow:0 -2px 8px #ccc;padding:8px 16px;z-index:99;display:flex;align-items:center;">
    <b>{{ song.title }}</b> - {{ song.artist }}
    <audio ref="audioRef" :src="song.audioUrl" controls style="margin-left:20px;" />
  </div>
</template>

<script setup lang="ts">
import { computed, nextTick, ref, watch } from 'vue'
import { usePlayerStore } from '../store/player'

const player = usePlayerStore()
const song = computed(() => player.currentSong)
const audioRef = ref<HTMLAudioElement|null>(null)

watch(
  () => player.currentSong,
  () => {
    nextTick(() => audioRef.value?.play())
  }
)
</script>