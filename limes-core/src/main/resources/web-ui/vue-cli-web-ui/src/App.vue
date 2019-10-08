<template>
	<div id="app">
		<MainApp/>
	</div>
</template>

<script>
import MainApp from './components/MainApp.vue'

export default {
	components: {
		MainApp
	}
}
</script>

<!-- <style lang="scss">

*, *::before, *::after {
	box-sizing: border-box;
}

#app {
	max-width: 400px;
	margin: 0 auto;
	line-height: 1.4;
	font-family: 'Avenir', Helvetica, Arial, sans-serif;
	-webkit-font-smoothing: antialiased;
	-moz-osx-font-smoothing: grayscale;
}

h1 {
	text-align: center;
}
</style> -->