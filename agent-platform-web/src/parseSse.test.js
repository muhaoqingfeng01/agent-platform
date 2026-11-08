import { describe, expect, it } from 'vitest'
import { parseSseBlock, splitSseChunks } from './parseSse.js'

describe('parseSse', () => {
  it('parses named token event', () => {
    const r = parseSseBlock('event: token\ndata: 你好')
    expect(r.event).toBe('token')
    expect(r.data).toBe('你好')
  })

  it('parses json data', () => {
    const r = parseSseBlock('event: done\ndata: {"status":"completed","tokens":3}')
    expect(r.event).toBe('done')
    expect(r.data.tokens).toBe(3)
  })

  it('falls back to json event field', () => {
    const r = parseSseBlock('data: {"event":"thinking","message":"ok"}')
    expect(r.event).toBe('thinking')
  })

  it('splits chunks by blank line', () => {
    const { blocks, rest } = splitSseChunks('event: a\ndata: 1\n\nevent: b\ndata: 2')
    expect(blocks).toHaveLength(1)
    expect(rest.startsWith('event: b')).toBe(true)
  })

  it('keeps incomplete rest', () => {
    const { blocks, rest } = splitSseChunks('event: token\ndata: hi')
    expect(blocks).toEqual([])
    expect(rest).toContain('token')
  })

  it('parses stream_started', () => {
    const r = parseSseBlock('event: stream_started\ndata: {"streamId":"abc","conversationId":"c1"}')
    expect(r.event).toBe('stream_started')
    expect(r.data.streamId).toBe('abc')
  })

  it('parses thinking event', () => {
    const r = parseSseBlock('event: thinking\ndata: "正在分析"')
    expect(r.event).toBe('thinking')
    expect(r.data).toBe('正在分析')
  })
})
