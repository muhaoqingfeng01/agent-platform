import { describe, expect, it } from 'vitest';
import { parseSseBlock, splitSseChunks } from './parseSse';

describe('parseSseBlock', () => {
  it('解析 named event + 字符串 data', () => {
    const frame = parseSseBlock('event: token\ndata: 你好');
    expect(frame?.event).toBe('token');
    expect(frame?.data).toBe('你好');
  });

  it('解析 JSON data（done）', () => {
    const frame = parseSseBlock(
      'event: done\ndata: {"status":"completed","tokens":12,"messageId":"msg_1"}',
    );
    expect(frame?.event).toBe('done');
    expect(frame?.data).toEqual({ status: 'completed', tokens: 12, messageId: 'msg_1' });
  });

  it('忽略 ping', () => {
    const frame = parseSseBlock('event: ping\ndata:');
    expect(frame?.event).toBe('ping');
  });

  it('兼容 data JSON 内嵌 event 字段', () => {
    const frame = parseSseBlock('data: {"event":"thinking","data":"正在检索"}');
    expect(frame?.event).toBe('thinking');
    expect(frame?.data).toBe('正在检索');
  });
});

describe('splitSseChunks', () => {
  it('按空行切帧并保留半包', () => {
    const { frames, rest } = splitSseChunks('event: token\ndata: a\n\nevent: token\ndata: b');
    expect(frames).toHaveLength(1);
    expect(frames[0].data).toBe('a');
    expect(rest).toContain('event: token');
  });
});
