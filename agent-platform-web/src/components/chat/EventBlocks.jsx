const MODE_LABEL = {
  CONVERSATION: '智能对话',
  KNOWLEDGE_SEARCH: '知识检索',
  TASK_EXECUTION: '任务执行',
  ANALYSIS: '分析推理',
  APPROVAL: '安全审批',
}

function formatMode(code) {
  return MODE_LABEL[code] || code
}

export default function EventBlocks({ events = [] }) {
  if (!events.length) return null
  return (
    <div>
      {events.map((ev, idx) => {
        if (ev.type === 'thinking') {
          return (
            <div key={idx} className="event-block">
              思考 · {typeof ev.data === 'string' ? ev.data : ev.data?.message || '…'}
            </div>
          )
        }
        if (ev.type === 'intent') {
          const code = ev.data?.intentCode || 'UNKNOWN'
          const suggested = ev.data?.suggestedMode
          // 未识别意图不展示，避免「UNKNOWN → CONVERSATION」干扰阅读
          if (!code || code === 'UNKNOWN') return null
          const modeHint =
            suggested && suggested !== 'CONVERSATION' ? ` → 建议${formatMode(suggested)}` : ''
          return (
            <div key={idx} className="event-block">
              意图 · {code}
              {modeHint}
            </div>
          )
        }
        if (ev.type === 'references') {
          const refs = Array.isArray(ev.data) ? ev.data : ev.data?.documents || ev.data?.items || []
          return (
            <div key={idx} className="event-block">
              引用 · {refs.length ? refs.map((r) => r.name || r.documentName || r.title || '文档').join('、') : '无'}
            </div>
          )
        }
        if (ev.type === 'task_plan' || ev.type === 'task_step') {
          return (
            <div key={idx} className="event-block">
              任务 · {ev.type === 'task_plan' ? `计划 ${ev.data?.totalSteps || 0} 步` : `${ev.data?.action || ''} ${ev.data?.status || ''}`}
            </div>
          )
        }
        if (ev.type === 'tool_call') {
          const name = ev.data?.tool || ev.data?.toolId || '工具'
          const params = ev.data?.params
          const paramHint =
            params && typeof params === 'object' && Object.keys(params).length
              ? ` · ${JSON.stringify(params).slice(0, 120)}`
              : ''
          return (
            <div key={idx} className="event-block">
              调用 · {name}
              {paramHint}
            </div>
          )
        }
        if (ev.type === 'tool_result') {
          const name = ev.data?.tool || ev.data?.toolId || '工具'
          const ok = ev.data?.success !== false && ev.data?.status !== 'failed'
          let summary = ''
          if (!ok) {
            summary = ev.data?.errorMessage || '失败'
          } else if (ev.data?.result != null) {
            const raw = typeof ev.data.result === 'string' ? ev.data.result : JSON.stringify(ev.data.result)
            summary = raw.length > 160 ? `${raw.slice(0, 160)}…` : raw
          } else {
            summary = '完成'
          }
          return (
            <div key={idx} className="event-block">
              结果 · {name} · {summary}
            </div>
          )
        }
        if (ev.type === 'analysis_table') {
          return (
            <div key={idx} className="event-block">
              分析表 · {ev.data?.title || '数据'} {ev.data?.empty ? '（空）' : ''}
            </div>
          )
        }
        return null
      })}
    </div>
  )
}
