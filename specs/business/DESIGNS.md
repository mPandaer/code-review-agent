# DESIGNS — index.html 视觉设计规范

> 参考风格：https://motherduck.com/
>
> 核心美学：**极简、硬边、高对比、暖色调底色、无圆角无阴影**

---

## 1. 设计令牌（Atomic CSS 变量）

以下 CSS 变量直接定义在 `:root`，后续布局组件只使用这些变量，确保风格统一。

```css
:root {
  /* === 背景色 === */
  --md-bg-page:        #F4EFEA;   /* 页面底色（暖奶油） */
  --md-bg-white:       #FFFFFF;   /* 卡片/区块白底 */
  --md-bg-off-white:   #F8F8F7;   /* 次级白底 */
  --md-bg-charcoal:    #383838;   /* 深色底色（用于反差区块） */

  /* === 文本色 === */
  --md-text-primary:   #383838;   /* 正文、标题 */
  --md-text-secondary: #666666;   /* 辅助文字 */
  --md-text-muted:     #A1A1A1;   /* 弱化文字 */
  --md-text-on-dark:   #FFFFFF;   /* 深色底上的文字 */

  /* === 强调色（卡片/区块背景） === */
  --md-accent-blue:    #97D4FF;   /* 浅蓝 */
  --md-accent-teal:    #53DBC9;   /* 青绿 */
  --md-accent-purple:  #F7F1FF;   /* 浅紫 */
  --md-accent-yellow:  #FFDD00;   /* 柠黄 */
  --md-accent-coral:   #FF7169;   /* 珊瑚红 */
  --md-accent-mint:    #F9FBE7;   /* 浅薄荷 */
  --md-accent-sky:     #6FC2FF;   /* 天蓝 */

  /* === 边框 === */
  --md-border-width:   2px;
  --md-border-color:   #383838;
  --md-border:         var(--md-border-width) solid var(--md-border-color);

  /* === 间距 === */
  --md-space-xs:       8px;
  --md-space-sm:       16px;
  --md-space-md:       24px;
  --md-space-lg:       40px;
  --md-space-xl:       64px;
  --md-space-2xl:      96px;

  /* === 字体 === */
  --md-font-mono:      "IBM Plex Mono", "Courier New", monospace;
  --md-font-sans:      -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif;

  /* === 字号 === */
  --md-text-h1:        48px;
  --md-text-h2:        32px;
  --md-text-h3:        24px;
  --md-text-h4:        20px;
  --md-text-body:      16px;
  --md-text-sm:        14px;
  --md-text-xs:        12px;

  /* === 行高 === */
  --md-leading-tight:  1.2;
  --md-leading-normal: 1.5;
  --md-leading-relaxed: 1.7;
}
```

---

## 2. 原子工具类

```css
/* === 排版 === */
.md-font-mono  { font-family: var(--md-font-mono); }
.md-font-sans  { font-family: var(--md-font-sans); }
.md-text-h1    { font-size: var(--md-text-h1); line-height: var(--md-leading-tight); font-weight: 400; }
.md-text-h2    { font-size: var(--md-text-h2); line-height: var(--md-leading-tight); font-weight: 400; }
.md-text-h3    { font-size: var(--md-text-h3); line-height: var(--md-leading-tight); font-weight: 400; }
.md-text-h4    { font-size: var(--md-text-h4); line-height: var(--md-leading-tight); font-weight: 600; }
.md-text-body  { font-size: var(--md-text-body); line-height: var(--md-leading-normal); }
.md-text-sm    { font-size: var(--md-text-sm); line-height: var(--md-leading-normal); }
.md-text-muted { color: var(--md-text-muted); }
.md-text-label { font-size: var(--md-text-sm); font-weight: 600; letter-spacing: 0.05em; text-transform: uppercase; }

/* === 颜色 === */
.md-color-primary   { color: var(--md-text-primary); }
.md-color-secondary { color: var(--md-text-secondary); }

/* === 边框 === */
.md-border       { border: var(--md-border); }
.md-border-top   { border-top: var(--md-border); }
.md-border-bottom { border-bottom: var(--md-border); }

/* === 间距 === */
.md-p-sm  { padding: var(--md-space-sm); }
.md-p-md  { padding: var(--md-space-md); }
.md-p-lg  { padding: var(--md-space-lg); }
.md-mt-lg { margin-top: var(--md-space-lg); }
.md-mb-lg { margin-bottom: var(--md-space-lg); }
```

---

## 3. 布局组件

### 3.1 页面框架

```
┌──────────────────────────────────────────┐
│  Header (顶部导航条)                      │
│  ─ 左侧: 项目名                           │
│  ─ 右侧: 文档数量标签                     │
├──────────────────────────────────────────┤
│                                          │
│  Hero 区                                 │
│  ─ 大标题 (H1, mono, 48px)              │
│  ─ 副标题 (body, muted)                 │
│  ─ 分割线 (2px solid)                   │
│                                          │
├──────────────────────────────────────────┤
│                                          │
│  Section 1: 架构设计                     │
│  ─ 标题行 + 卡片网格                     │
│                                          │
├──────────────────────────────────────────┤
│  Section 2: 技术选型                     │
│  ─ 标题行 + 卡片网格                     │
│                                          │
├──────────────────────────────────────────┤
│  Footer                                  │
│  ─ 轻量版权 + 最后更新时间               │
└──────────────────────────────────────────┘
```

### 3.2 标题行组件

每个 Section 顶部固定的标题行结构：

```html
<div class="md-section-header">
  <span class="md-text-label md-color-secondary">SECTION 01</span>
  <h2 class="md-text-h2 md-font-mono md-color-primary">架构设计</h2>
  <div class="md-section-divider"></div>
</div>
```

CSS：
```css
.md-section-header {
  padding: var(--md-space-lg) 0 var(--md-space-md);
}
.md-section-divider {
  width: 100%;
  height: 0;
  border-bottom: var(--md-border);
  margin-top: var(--md-space-sm);
}
```

### 3.3 卡片组件

方形硬边卡片，无圆角无阴影：

```html
<div class="md-card">
  <div class="md-card-label md-text-label md-color-secondary">设计决策</div>
  <h3 class="md-card-title md-text-h3 md-font-mono">Agent 三层架构</h3>
  <p class="md-card-body md-text-body md-color-secondary">
    CLI 层 → Agent Core 层 → LLM Client 层
  </p>
</div>
```

CSS：
```css
.md-card {
  background: var(--md-bg-white);
  border: var(--md-border);
  padding: var(--md-space-md);
  /* 无 border-radius, 无 box-shadow */
}
.md-card:hover {
  background: var(--md-accent-blue);  /* 可选交互 */
}
.md-card-title {
  margin: var(--md-space-xs) 0;
}
.md-card-body {
  margin: 0;
}
```

### 3.4 卡片网格

```css
.md-card-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: var(--md-space-md);
}
```

### 3.5 信息行组件（用于技术选型对比）

```html
<div class="md-info-row">
  <div class="md-info-label md-text-label md-color-secondary">JSON 库</div>
  <div class="md-info-value md-text-h4 md-font-mono md-color-primary">Jackson</div>
  <div class="md-info-reason md-text-body md-color-secondary">生态成熟，Spring 生态默认选择</div>
</div>
```

CSS：
```css
.md-info-row {
  display: grid;
  grid-template-columns: 160px 200px 1fr;
  gap: var(--md-space-md);
  align-items: baseline;
  padding: var(--md-space-sm) 0;
  border-bottom: 1px solid var(--md-text-muted);
}
```

---

## 4. 配色轮换规则

当卡片或区块顺序排列时，按以下顺序轮换 accent 背景色：

| 序号 | 颜色变量 | 色值 |
|------|---------|------|
| 1 | `--md-accent-blue` | `#97D4FF` |
| 2 | `--md-accent-teal` | `#53DBC9` |
| 3 | `--md-accent-purple` | `#F7F1FF` |
| 4 | `--md-accent-mint` | `#F9FBE7` |

用 `nth-child` 实现：
```css
.md-card-grid > .md-card:nth-child(4n+1) { background: var(--md-accent-blue); }
.md-card-grid > .md-card:nth-child(4n+2) { background: var(--md-accent-teal); }
.md-card-grid > .md-card:nth-child(4n+3) { background: var(--md-accent-purple); }
.md-card-grid > .md-card:nth-child(4n+4) { background: var(--md-accent-mint); }
```

---

## 5. 响应式断点

仅两个断点，保持简单：

```css
/* ≥ 768px：双栏布局 */
@media (min-width: 768px) {
  .md-card-grid { grid-template-columns: repeat(2, 1fr); }
}

/* < 768px：单栏堆叠 */
@media (max-width: 767px) {
  :root {
    --md-text-h1: 32px;
    --md-text-h2: 24px;
  }
  .md-info-row {
    grid-template-columns: 1fr;
  }
}
```
