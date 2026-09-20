import{L as s,M as r,N as a,P as i,O as c}from"./ionic-BiQLqjFv.js";/*!
 * (C) Ionic http://ionicframework.com - MIT License
 */const m=()=>{const e=window;e.addEventListener("statusTap",(()=>{s((()=>{const n=document.elementFromPoint(e.innerWidth/2,e.innerHeight/2);if(!n)return;const t=r(n);t&&new Promise((o=>a(t,o))).then((()=>{i((async()=>{t.style.setProperty("--overflow","hidden"),await c(t,300),t.style.removeProperty("--overflow")}))}))}))}))};export{m as startStatusTap};
