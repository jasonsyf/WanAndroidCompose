#!/usr/bin/env python3
from __future__ import annotations
import re
import zipfile
from pathlib import Path
import xml.etree.ElementTree as ET


P_NS = "http://schemas.openxmlformats.org/presentationml/2006/main"
A_NS = "http://schemas.openxmlformats.org/drawingml/2006/main"
R_NS = "http://schemas.openxmlformats.org/officeDocument/2006/relationships"
REL_NS = "http://schemas.openxmlformats.org/package/2006/relationships"
CT_NS = "http://schemas.openxmlformats.org/package/2006/content-types"

ET.register_namespace("a", A_NS)
ET.register_namespace("p", P_NS)
ET.register_namespace("r", R_NS)


def qn(ns: str, tag: str) -> str:
    return f"{{{ns}}}{tag}"


def ptag(tag: str) -> str:
    return qn(P_NS, tag)


def atag(tag: str) -> str:
    return qn(A_NS, tag)


def rtag(tag: str) -> str:
    return qn(R_NS, tag)


def reltag(tag: str) -> str:
    return qn(REL_NS, tag)


SLIDE_W = 9_144_000
SLIDE_H = 5_143_500

RED = "B31B1B"
RED_DARK = "8F1111"
GOLD = "D9A441"
GOLD_LIGHT = "F6E3B4"
GOLD_PALE = "FFF7E7"
TEXT = "2D2320"
GRAY = "6B5F5A"
LINE = "E2C788"
WHITE = "FFFFFF"
CORAL = "F2B7A8"
AMBER = "E8C86E"
TEAL = "8CCAC8"
ORANGE = "E9A26B"
SAND = "F5D8C6"
BLUE = "9FD0E9"


TEMPLATE = Path(
    "/Users/sunyufeng/Library/Containers/com.tencent.xinWeChat/Data/Documents/"
    "xwechat_files/wxid_9rv5nfo0vjpn22_dfdf/temp/RWTemp/2026-04/"
    "4e2ca98f72441d1d10bd5505e09454e1/2026PPT新模版 .pptx"
)
OUTPUT = Path("/tmp/总账会计工作汇报-2025总结及2026规划.pptx")


def parse_xml(data: bytes) -> ET.Element:
    return ET.fromstring(data)


def xml_bytes(root: ET.Element) -> bytes:
    return ET.tostring(root, encoding="utf-8", xml_declaration=True)


def rels_bytes(root: ET.Element) -> bytes:
    return ET.tostring(root, encoding="utf-8", xml_declaration=True)


def text_runs(shape: ET.Element):
    return shape.findall(".//a:t", {"a": A_NS})


def find_shape(root: ET.Element, shape_id: str) -> ET.Element:
    for sp in root.findall(".//p:sp", {"p": P_NS}):
        c_nv = sp.find("p:nvSpPr/p:cNvPr", {"p": P_NS})
        if c_nv is not None and c_nv.attrib.get("id") == shape_id:
            return sp
    raise KeyError(f"shape {shape_id} not found")


def ensure_tx_body(shape: ET.Element) -> ET.Element:
    tx = shape.find("p:txBody", {"p": P_NS})
    if tx is None:
        tx = ET.SubElement(shape, ptag("txBody"))
        ET.SubElement(tx, atag("bodyPr"))
        ET.SubElement(tx, atag("lstStyle"))
    if tx.find(atag("bodyPr")) is None:
        tx.insert(0, ET.Element(atag("bodyPr")))
    if tx.find(atag("lstStyle")) is None:
        tx.insert(1, ET.Element(atag("lstStyle")))
    return tx


def clear_paragraphs(tx_body: ET.Element):
    for p in list(tx_body.findall(atag("p"))):
        tx_body.remove(p)


def add_paragraph(
    tx_body: ET.Element,
    text: str,
    *,
    size: int = 1800,
    bold: bool = False,
    color: str = TEXT,
    align: str = "l",
    latin: str = "微软雅黑",
    level: int | None = None,
):
    p = ET.SubElement(tx_body, atag("p"))
    p_pr = ET.SubElement(p, atag("pPr"))
    p_pr.set("algn", align)
    if level is not None:
        p_pr.set("lvl", str(level))
    ln_spc = ET.SubElement(p_pr, atag("lnSpc"))
    ET.SubElement(ln_spc, atag("spcPct"), {"val": "103000"})
    r = ET.SubElement(p, atag("r"))
    r_pr = ET.SubElement(
        r,
        atag("rPr"),
        {"lang": "zh-CN", "sz": str(size), "b": "1" if bold else "0"},
    )
    solid = ET.SubElement(r_pr, atag("solidFill"))
    ET.SubElement(solid, atag("srgbClr"), {"val": color})
    ET.SubElement(r_pr, atag("latin"), {"typeface": latin})
    ET.SubElement(r_pr, atag("ea"), {"typeface": latin})
    t = ET.SubElement(r, atag("t"))
    t.text = text
    end = ET.SubElement(
        p,
        atag("endParaRPr"),
        {"lang": "zh-CN", "sz": str(size), "b": "1" if bold else "0"},
    )
    end_fill = ET.SubElement(end, atag("solidFill"))
    ET.SubElement(end_fill, atag("srgbClr"), {"val": color})


def set_shape_lines(shape: ET.Element, lines: list[str], *, size: int, bold: bool = False, color: str = TEXT):
    tx = ensure_tx_body(shape)
    clear_paragraphs(tx)
    for line in lines:
        add_paragraph(tx, line, size=size, bold=bold, color=color)


def remove_shape(root: ET.Element, shape_id: str):
    sp_tree = root.find(".//p:spTree", {"p": P_NS})
    if sp_tree is None:
        return
    for sp in list(sp_tree.findall("p:sp", {"p": P_NS})):
        c_nv = sp.find("p:nvSpPr/p:cNvPr", {"p": P_NS})
        if c_nv is not None and c_nv.attrib.get("id") == shape_id:
            sp_tree.remove(sp)
            return


def replace_cover_info(shape: ET.Element):
    tx = ensure_tx_body(shape)
    clear_paragraphs(tx)
    for line in ["汇报部门：电商财务处", "汇报岗位：总账会计", "时间：2026年4月"]:
        add_paragraph(tx, line, size=1800, bold=False, color=WHITE)


def set_chapter_slide(root: ET.Element, numeral: str, title: str, bullets: list[str]):
    title_shape = find_shape(root, "3")
    title_runs = text_runs(title_shape)
    while len(title_runs) < 3:
        set_shape_lines(title_shape, [f"{numeral}.{title}"], size=2600, bold=True, color=WHITE)
        break
    else:
        title_runs[0].text = numeral
        title_runs[1].text = "."
        title_runs[2].text = title
    bullet_shape = find_shape(root, "2")
    tx = ensure_tx_body(bullet_shape)
    clear_paragraphs(tx)
    for bullet in bullets[:4]:
        p = ET.SubElement(tx, atag("p"))
        p_pr = ET.SubElement(p, atag("pPr"), {"marL": "285750", "indent": "-285750"})
        ln = ET.SubElement(p_pr, atag("lnSpc"))
        ET.SubElement(ln, atag("spcPct"), {"val": "150000"})
        bu_clr = ET.SubElement(p_pr, atag("buClr"))
        ET.SubElement(bu_clr, atag("srgbClr"), {"val": "FFE699"})
        ET.SubElement(p_pr, atag("buFont"), {"typeface": "Arial", "pitchFamily": "34", "charset": "0"})
        ET.SubElement(p_pr, atag("buChar"), {"char": "•"})
        r = ET.SubElement(p, atag("r"))
        r_pr = ET.SubElement(r, atag("rPr"), {"lang": "zh-CN", "sz": "1400"})
        fill = ET.SubElement(r_pr, atag("solidFill"))
        ET.SubElement(fill, atag("schemeClr"), {"val": "bg1"})
        ET.SubElement(r, atag("t")).text = bullet
        end = ET.SubElement(p, atag("endParaRPr"), {"lang": "zh-CN", "sz": "1400"})
        end_fill = ET.SubElement(end, atag("solidFill"))
        ET.SubElement(end_fill, atag("schemeClr"), {"val": "bg1"})


def shape_base(shape_id: int, name: str) -> ET.Element:
    sp = ET.Element(ptag("sp"))
    nv = ET.SubElement(sp, ptag("nvSpPr"))
    ET.SubElement(nv, ptag("cNvPr"), {"id": str(shape_id), "name": name})
    ET.SubElement(nv, ptag("cNvSpPr"), {"txBox": "1"})
    ET.SubElement(nv, ptag("nvPr"))
    return sp


def set_shape_geometry(
    sp: ET.Element,
    x: int,
    y: int,
    cx: int,
    cy: int,
    *,
    fill: str | None = None,
    line: str | None = None,
    round_rect: bool = False,
    geom: str = "rect",
    line_width: int = 19050,
    no_fill: bool = False,
):
    sp_pr = ET.SubElement(sp, ptag("spPr"))
    xfrm = ET.SubElement(sp_pr, atag("xfrm"))
    ET.SubElement(xfrm, atag("off"), {"x": str(x), "y": str(y)})
    ET.SubElement(xfrm, atag("ext"), {"cx": str(cx), "cy": str(cy)})
    ET.SubElement(sp_pr, atag("prstGeom"), {"prst": "roundRect" if round_rect else geom})
    sp_pr.find(atag("prstGeom")).append(ET.Element(atag("avLst")))
    if no_fill or fill is None:
        ET.SubElement(sp_pr, atag("noFill"))
    else:
        solid = ET.SubElement(sp_pr, atag("solidFill"))
        ET.SubElement(solid, atag("srgbClr"), {"val": fill})
    ln = ET.SubElement(sp_pr, atag("ln"), {"w": str(line_width)})
    if line is None:
        ET.SubElement(ln, atag("noFill"))
    else:
        ln_fill = ET.SubElement(ln, atag("solidFill"))
        ET.SubElement(ln_fill, atag("srgbClr"), {"val": line})


def add_text_shape(
    sp_tree: ET.Element,
    shape_id: int,
    name: str,
    x: int,
    y: int,
    cx: int,
    cy: int,
    paragraphs: list[dict],
    *,
    fill: str | None = None,
    line: str | None = None,
    round_rect: bool = False,
    geom: str = "rect",
    no_fill: bool = False,
    anchor: str = "t",
    margin: tuple[int, int, int, int] = (91440, 60000, 91440, 60000),
) -> int:
    sp = shape_base(shape_id, name)
    set_shape_geometry(sp, x, y, cx, cy, fill=fill, line=line, round_rect=round_rect, geom=geom, no_fill=no_fill)
    tx = ET.SubElement(sp, ptag("txBody"))
    body = ET.SubElement(
        tx,
        atag("bodyPr"),
        {
            "wrap": "square",
            "anchor": anchor,
            "lIns": str(margin[0]),
            "tIns": str(margin[1]),
            "rIns": str(margin[2]),
            "bIns": str(margin[3]),
        },
    )
    ET.SubElement(body, atag("normAutofit"))
    ET.SubElement(tx, atag("lstStyle"))
    for para in paragraphs:
        add_paragraph(
            tx,
            para["text"],
            size=para.get("size", 1700),
            bold=para.get("bold", False),
            color=para.get("color", TEXT),
            align=para.get("align", "l"),
        )
    sp_tree.append(sp)
    return shape_id + 1


def add_card(sp_tree: ET.Element, shape_id: int, x: int, y: int, w: int, h: int, title: str, lines: list[str]) -> int:
    paragraphs = [{"text": title, "size": 1550, "bold": True, "color": RED_DARK}]
    paragraphs.extend({"text": f"• {line}", "size": 1150, "color": TEXT} for line in lines[:2])
    return add_text_shape(
        sp_tree,
        shape_id,
        title,
        x,
        y,
        w,
        h,
        paragraphs,
        fill="FFF9EF",
        line=None,
        round_rect=True,
        margin=(140000, 90000, 140000, 90000),
    )


def add_metric_card(sp_tree: ET.Element, shape_id: int, x: int, y: int, w: int, h: int, value: str, label: str) -> int:
    paragraphs = [
        {"text": value, "size": 2600, "bold": True, "color": RED_DARK, "align": "ctr"},
        {"text": label, "size": 1300, "color": GRAY, "align": "ctr"},
    ]
    return add_text_shape(
        sp_tree,
        shape_id,
        label,
        x,
        y,
        w,
        h,
        paragraphs,
        fill=WHITE,
        line=LINE,
        round_rect=True,
        margin=(60000, 80000, 60000, 80000),
    )


def blank_slide_root() -> ET.Element:
    root = ET.Element(ptag("sld"))
    c_sld = ET.SubElement(root, ptag("cSld"), {"name": "Blank Custom"})
    sp_tree = ET.SubElement(c_sld, ptag("spTree"))
    nv_grp = ET.SubElement(sp_tree, ptag("nvGrpSpPr"))
    ET.SubElement(nv_grp, ptag("cNvPr"), {"id": "1", "name": ""})
    ET.SubElement(nv_grp, ptag("cNvGrpSpPr"))
    ET.SubElement(nv_grp, ptag("nvPr"))
    grp = ET.SubElement(sp_tree, ptag("grpSpPr"))
    xfrm = ET.SubElement(grp, atag("xfrm"))
    ET.SubElement(xfrm, atag("off"), {"x": "0", "y": "0"})
    ET.SubElement(xfrm, atag("ext"), {"cx": "0", "cy": "0"})
    ET.SubElement(xfrm, atag("chOff"), {"x": "0", "y": "0"})
    ET.SubElement(xfrm, atag("chExt"), {"cx": "0", "cy": "0"})
    clr = ET.SubElement(root, ptag("clrMapOvr"))
    ET.SubElement(clr, atag("masterClrMapping"))
    return root


def body_slide_root() -> ET.Element:
    with zipfile.ZipFile(TEMPLATE, "r") as zf:
        root = parse_xml(zf.read("ppt/slides/slide4.xml"))
    for cust in list(root.findall(".//p:custDataLst", {"p": P_NS})):
        for elem in root.iter():
            if cust in list(elem):
                elem.remove(cust)
                break
    remove_shape(root, "2")
    remove_shape(root, "3")
    return root


def blank_slide_rels(layout_idx: int = 2) -> bytes:
    root = ET.Element(reltag("Relationships"))
    ET.SubElement(
        root,
        reltag("Relationship"),
        {
            "Id": "rId1",
            "Type": "http://schemas.openxmlformats.org/officeDocument/2006/relationships/slideLayout",
            "Target": f"../slideLayouts/slideLayout{layout_idx}.xml",
        },
    )
    return rels_bytes(root)


def add_header(root: ET.Element, sp_tree: ET.Element, label: str, title: str, subtitle: str | None = None) -> int:
    set_shape_lines(find_shape(root, "4"), [title], size=2400, bold=True, color=WHITE)
    sid = 20
    sid = add_text_shape(
        sp_tree,
        sid,
        "HeaderTag",
        620000,
        620000,
        1180000,
        220000,
        [{"text": label, "size": 1200, "bold": True, "color": WHITE, "align": "ctr"}],
        fill=RED_DARK,
        line=RED_DARK,
        round_rect=True,
        anchor="ctr",
        margin=(30000, 30000, 30000, 30000),
    )
    sid = add_text_shape(
        sp_tree,
        sid,
        "Divider",
        620000,
        945000,
        980000,
        18000,
        [],
        fill=GOLD,
        line=GOLD,
    )
    if subtitle:
        sid = add_text_shape(
            sp_tree,
            sid,
            "Subtitle",
            620000,
            1080000,
            7600000,
            230000,
            [{"text": subtitle, "size": 1200, "color": GRAY}],
            no_fill=True,
            line=None,
            margin=(0, 0, 0, 0),
        )
    return sid


def add_content_panel(sp_tree: ET.Element, sid: int, y: int = 1640000, h: int = 2850000) -> int:
    sid = add_text_shape(
        sp_tree,
        sid,
        "ContentPanel",
        560000,
        y,
        8040000,
        h,
        [],
        fill="FFFDF8",
        line=None,
        round_rect=True,
    )
    return sid


def add_badge(sp_tree: ET.Element, sid: int, x: int, y: int, text: str, fill: str = RED_DARK) -> int:
    return add_text_shape(
        sp_tree,
        sid,
        "Badge",
        x,
        y,
        260000,
        260000,
        [{"text": text, "size": 1200, "bold": True, "color": WHITE, "align": "ctr"}],
        fill=fill,
        line=None,
        geom="ellipse",
        anchor="ctr",
        margin=(20000, 20000, 20000, 20000),
    )


def build_process_slide(
    label: str,
    title: str,
    subtitle: str,
    lead: tuple[str, list[str]],
    steps: list[tuple[str, str]],
    footer: tuple[str, list[str]],
) -> tuple[bytes, bytes]:
    root = body_slide_root()
    sp_tree = root.find(".//p:spTree", {"p": P_NS})
    sid = add_header(root, sp_tree, label, title, subtitle)
    sid = add_content_panel(sp_tree, sid, 1360000, 3060000)
    sid = add_card(sp_tree, sid, 650000, 1540000, 7580000, 520000, lead[0], lead[1])
    step_x = 760000
    for idx, (step_title, step_desc) in enumerate(steps[:4], start=1):
        sid = add_badge(sp_tree, sid, step_x, 2460000, str(idx), RED_DARK if idx % 2 else GOLD)
        sid = add_text_shape(
            sp_tree,
            sid,
            f"Step{idx}",
            step_x - 30000,
            2780000,
            1500000,
            760000,
            [
                {"text": step_title, "size": 1450, "bold": True, "color": RED_DARK, "align": "ctr"},
                {"text": step_desc, "size": 1100, "color": TEXT, "align": "ctr"},
            ],
            fill="FFF9EF",
            line=None,
            round_rect=True,
            anchor="ctr",
            margin=(70000, 50000, 70000, 50000),
        )
        if idx < len(steps[:4]):
            sid = add_text_shape(
                sp_tree,
                sid,
                "Arrow",
                step_x + 1350000,
                2970000,
                350000,
                180000,
                [{"text": "→", "size": 2200, "bold": True, "color": GOLD, "align": "ctr"}],
                no_fill=True,
                line=None,
                anchor="ctr",
                margin=(0, 0, 0, 0),
            )
        step_x += 1850000
    sid = add_card(sp_tree, sid, 650000, 3790000, 7580000, 360000, footer[0], footer[1])
    return xml_bytes(root), blank_slide_rels()


def build_pillar_slide(label: str, title: str, subtitle: str, pillars: list[tuple[str, list[str]]]) -> tuple[bytes, bytes]:
    root = body_slide_root()
    sp_tree = root.find(".//p:spTree", {"p": P_NS})
    sid = add_header(root, sp_tree, label, title, subtitle)
    sid = add_content_panel(sp_tree, sid, 1360000, 3060000)
    xs = [650000, 3260000, 5870000]
    ws = [2300000, 2300000, 2290000]
    fills = ["FFF7EC", "FFF9F2", "FFF7EC"]
    for idx, ((pillar_title, pillar_lines), x, w, fill) in enumerate(zip(pillars[:3], xs, ws, fills), start=1):
        sid = add_badge(sp_tree, sid, x + 90000, 1560000, str(idx), RED_DARK)
        sid = add_text_shape(
            sp_tree,
            sid,
            f"Pillar{idx}",
            x,
            1720000,
            w,
            2350000,
            [{"text": pillar_title, "size": 1550, "bold": True, "color": RED_DARK}]
            + [{"text": f"• {line}", "size": 1180, "color": TEXT} for line in pillar_lines[:4]],
            fill=fill,
            line=None,
            round_rect=True,
            margin=(120000, 90000, 120000, 90000),
        )
    return xml_bytes(root), blank_slide_rels()


def build_compare_slide(
    label: str,
    title: str,
    subtitle: str,
    left: tuple[str, list[str]],
    right: tuple[str, list[str]],
    footer: tuple[str, list[str]],
) -> tuple[bytes, bytes]:
    root = body_slide_root()
    sp_tree = root.find(".//p:spTree", {"p": P_NS})
    sid = add_header(root, sp_tree, label, title, subtitle)
    sid = add_content_panel(sp_tree, sid, 1360000, 3060000)
    sid = add_card(sp_tree, sid, 650000, 1580000, 3660000, 1880000, left[0], left[1])
    sid = add_card(sp_tree, sid, 4420000, 1580000, 3660000, 1880000, right[0], right[1])
    sid = add_card(sp_tree, sid, 650000, 3590000, 7580000, 480000, footer[0], footer[1])
    return xml_bytes(root), blank_slide_rels()


def build_stakeholder_slide(
    label: str,
    title: str,
    subtitle: str,
    center_title: str,
    center_lines: list[str],
    outer_cards: list[tuple[str, list[str]]],
) -> tuple[bytes, bytes]:
    root = body_slide_root()
    sp_tree = root.find(".//p:spTree", {"p": P_NS})
    sid = add_header(root, sp_tree, label, title, subtitle)
    sid = add_content_panel(sp_tree, sid, 1360000, 3060000)
    coords = [
        (650000, 1650000, 2400000, 900000),
        (5760000, 1650000, 2320000, 900000),
        (650000, 3160000, 2400000, 900000),
        (5760000, 3160000, 2320000, 900000),
    ]
    sid = add_text_shape(
        sp_tree,
        sid,
        "CenterHub",
        3060000,
        2240000,
        2580000,
        1180000,
        [{"text": center_title, "size": 1550, "bold": True, "color": RED_DARK, "align": "ctr"}]
        + [{"text": line, "size": 1100, "color": TEXT, "align": "ctr"} for line in center_lines[:2]],
        fill="FFF8EE",
        line=None,
        round_rect=True,
        anchor="ctr",
        margin=(100000, 60000, 100000, 60000),
    )
    for (card_title, card_lines), (x, y, w, h) in zip(outer_cards[:4], coords):
        sid = add_card(sp_tree, sid, x, y, w, h, card_title, card_lines)
    return xml_bytes(root), blank_slide_rels()


def build_grid_cards_slide(label: str, title: str, subtitle: str, cards: list[tuple[str, list[str]]]) -> tuple[bytes, bytes]:
    root = body_slide_root()
    sp_tree = root.find(".//p:spTree", {"p": P_NS})
    sid = add_header(root, sp_tree, label, title, subtitle)
    sid = add_content_panel(sp_tree, sid, 1360000, 3060000)
    coords = [
        (650000, 1590000, 3660000, 1280000),
        (4420000, 1590000, 3660000, 1280000),
        (650000, 2970000, 3660000, 1280000),
        (4420000, 2970000, 3660000, 1280000),
    ]
    for (card_title, card_lines), (x, y, w, h) in zip(cards, coords):
        sid = add_card(sp_tree, sid, x, y, w, h, card_title, card_lines)
    return xml_bytes(root), blank_slide_rels()


def build_focus_cards_slide(
    label: str,
    title: str,
    subtitle: str,
    lead_card: tuple[str, list[str]],
    side_cards: list[tuple[str, list[str]]],
) -> tuple[bytes, bytes]:
    root = body_slide_root()
    sp_tree = root.find(".//p:spTree", {"p": P_NS})
    sid = add_header(root, sp_tree, label, title, subtitle)
    sid = add_content_panel(sp_tree, sid, 1360000, 3060000)
    sid = add_card(sp_tree, sid, 650000, 1590000, 3300000, 2660000, lead_card[0], lead_card[1])
    right_y = 1590000
    right_h = 820000
    for card_title, card_lines in side_cards[:3]:
        sid = add_card(sp_tree, sid, 4200000, right_y, 3880000, right_h, card_title, card_lines)
        right_y += right_h + 100000
    return xml_bytes(root), blank_slide_rels()


def build_top_band_slide(
    label: str,
    title: str,
    subtitle: str,
    top_card: tuple[str, list[str]],
    lower_cards: list[tuple[str, list[str]]],
) -> tuple[bytes, bytes]:
    root = body_slide_root()
    sp_tree = root.find(".//p:spTree", {"p": P_NS})
    sid = add_header(root, sp_tree, label, title, subtitle)
    sid = add_content_panel(sp_tree, sid, 1360000, 3060000)
    sid = add_card(
        sp_tree,
        sid,
        650000,
        1580000,
        7580000,
        720000,
        top_card[0],
        top_card[1],
    )
    coords = [
        (650000, 2450000, 2380000, 1800000),
        (3260000, 2450000, 2380000, 1800000),
        (5880000, 2450000, 2270000, 1800000),
    ]
    for (card_title, card_lines), (x, y, w, h) in zip(lower_cards, coords):
        sid = add_card(sp_tree, sid, x, y, w, h, card_title, card_lines)
    return xml_bytes(root), blank_slide_rels()


def build_overview_slide() -> tuple[bytes, bytes]:
    return build_pillar_slide(
        "岗位职责",
        "岗位职责与工作重点概览",
        "围绕总账核算、税务申报、专项协同和经营支持开展全年财务工作。",
        [
            (
                "岗位定位",
                [
                    "承接总账核算与专项事项协同。",
                    "连接业务、税务、报表与审计流程。",
                    "兼顾日常闭环与阶段性重点事项。",
                ],
            ),
            (
                "核心职责",
                [
                    "统筹结账、报表、统计及归档。",
                    "承担费用审核、预算管控与纳税申报。",
                    "保证账务、税务与资料口径一致。",
                ],
            ),
            (
                "专项支撑",
                [
                    "支撑三号仓核算、利润预测与年审。",
                    "推进更名、盘点、披露等专项事项。",
                    "对重点事项进行资料准备和过程跟进。",
                ],
            ),
        ],
    )


def build_summary_slide(title: str, lines_by_card: list[tuple[str, list[str]]], layout: str = "grid") -> tuple[bytes, bytes]:
    subtitle = "围绕主题提炼不同重点，按实际工作特征分层展示。"
    if layout == "focus":
        return build_focus_cards_slide("工作总结", title, subtitle, lines_by_card[0], lines_by_card[1:])
    if layout == "band":
        return build_top_band_slide("工作总结", title, subtitle, lines_by_card[0], lines_by_card[1:])
    return build_grid_cards_slide("工作总结", title, subtitle, lines_by_card)


def build_plan_slide(title: str, subtitle: str, lines_by_card: list[tuple[str, list[str]]], layout: str = "grid") -> tuple[bytes, bytes]:
    if layout == "focus":
        return build_focus_cards_slide("工作规划", title, subtitle, lines_by_card[0], lines_by_card[1:])
    if layout == "band":
        return build_top_band_slide("工作规划", title, subtitle, lines_by_card[0], lines_by_card[1:])
    return build_grid_cards_slide("工作规划", title, subtitle, lines_by_card)


def add_table(
    sp_tree: ET.Element,
    sid: int,
    x: int,
    y: int,
    col_widths: list[int],
    row_height: int,
    headers: list[str],
    rows: list[list[str]],
) -> int:
    for row_idx, row in enumerate([headers] + rows):
        current_x = x
        for col_idx, value in enumerate(row):
            fill = GOLD_LIGHT if row_idx == 0 else (WHITE if row_idx % 2 == 1 else GOLD_PALE)
            color = RED_DARK if row_idx == 0 else TEXT
            sid = add_text_shape(
                sp_tree,
                sid,
                "table-cell",
                current_x,
                y + row_idx * row_height,
                col_widths[col_idx],
                row_height,
                [{"text": value, "size": 1150, "bold": row_idx == 0, "color": color, "align": "ctr"}],
                fill=fill,
                line=LINE,
                anchor="ctr",
                margin=(20000, 20000, 20000, 20000),
            )
            current_x += col_widths[col_idx]
    return sid


def build_tax_plan_slide() -> tuple[bytes, bytes]:
    root = body_slide_root()
    sp_tree = root.find(".//p:spTree", {"p": P_NS})
    sid = add_header(
        root,
        sp_tree,
        "工作规划",
        "2026年工作规划（四）税务规划与利润调节思路",
        "结合主体盈利情况与可弥补亏损余额，统筹利润留存方向，提升整体税务筹划空间。",
    )
    sid = add_content_panel(sp_tree, sid, 1360000, 3060000)
    sid = add_card(
        sp_tree,
        sid,
        650000,
        1540000,
        7580000,
        520000,
        "规划背景",
        [
            "杭好留有较大可弥补亏损余额，但年营收较小；树上 2025 年营业利润较大，若 2026 年利润持续偏高，可弥补亏损额将逐步不够用于抵税。",
        ],
    )
    sid = add_text_shape(
        sp_tree,
        sid,
        "单位",
        6550000,
        2140000,
        1540000,
        180000,
        [{"text": "单位：万元", "size": 1150, "bold": True, "color": GRAY, "align": "r"}],
        no_fill=True,
        line=None,
        margin=(0, 0, 0, 0),
    )
    sid = add_table(
        sp_tree,
        sid,
        650000,
        2320000,
        [900000, 1650000, 1950000, 1750000, 2100000],
        300000,
        ["公司主体", "2025年主营收", "2025年利润总额", "2025年毛利率", "2026年初可弥补亏损"],
        [["杭好", "3.00", "3.00", "9%", "5.00"], ["树上", "2.00", "4.00", "27%", "6.00"]],
    )
    sid = add_card(
        sp_tree,
        sid,
        650000,
        3290000,
        3660000,
        980000,
        "规划动作",
        [
            "规划将部分店铺转移至杭好，优先利用杭好的可弥补亏损额。",
            "在合理范围内提高杭好向树上采购货款的单价，将更多利润留存于杭好。",
        ],
    )
    sid = add_card(
        sp_tree,
        sid,
        4420000,
        3290000,
        3660000,
        980000,
        "预期效果",
        [
            "平衡主体间利润分布，缓释树上后续所得税压力。",
            "提升可弥补亏损的使用效率，同时兼顾业务实质与价格合理性。",
        ],
    )
    return xml_bytes(root), blank_slide_rels()


def build_suggestion_slide() -> tuple[bytes, bytes]:
    root = body_slide_root()
    sp_tree = root.find(".//p:spTree", {"p": P_NS})
    sid = add_header(root, sp_tree, "建议事项", "个人建议及需协调事项", "建议围绕资料前置、系统联动和专项节点协同三方面持续优化。")
    sid = add_content_panel(sp_tree, sid, 1360000, 3060000)
    cards = [
        (
            620000,
            "建议一：共享平台优化",
            [
                "增加税务共享平台主管税务核对敏感功能的完善与优化。",
                "分思商城发票建议支持单独核对与处理，降低人工拆分识别成本。",
                "针对全电发票部分红冲间隔沉淀后在平台勾选不到的问题，推动系统优化。",
            ],
        ),
        (
            3240000,
            "建议二：资料前置共享",
            [
                "建议结账、报税、披露相关资料按月形成前置清单并提前传递。",
                "减少月末集中补资料，提高凭证审核和报表出具时效。",
                "重点事项可由业务、财务共同确认责任人与完成节点。",
            ],
        ),
        (
            5860000,
            "建议三：专项台账管理",
            [
                "对年审、汇算清缴、工商申报、盘点、披露及闭店库存事项建立年度节点表。",
                "提前明确资料要求、协同部门及输出口径，避免临近节点集中突击。",
                "对于临时监管或税局事项，建议预留应急响应窗口并形成闭环台账。",
            ],
        ),
    ]
    for x, title, lines in cards:
        sid = add_card(sp_tree, sid, x, 1760000, 2180000, 2300000, title, lines)
    return xml_bytes(root), blank_slide_rels()


def build_gantt_slide() -> tuple[bytes, bytes]:
    root = body_slide_root()
    sp_tree = root.find(".//p:spTree", {"p": P_NS})
    sid = add_header(root, sp_tree, "工作规划", "年度专项工作甘特图总览", "以年度关键节点统筹更名、年审、汇算、工商申报、盘点及披露支持。")
    sid = add_content_panel(sp_tree, sid, 1360000, 3060000)

    left_x = 650000
    top_y = 1650000
    label_w = 1850000
    month_w = 455000
    header_h = 280000
    row_h = 340000

    sid = add_text_shape(
        sp_tree,
        sid,
        "事项头",
        left_x,
        top_y,
        label_w,
        header_h,
        [{"text": "重点事项", "size": 1300, "bold": True, "color": WHITE, "align": "ctr"}],
        fill=RED_DARK,
        line=RED_DARK,
        anchor="ctr",
        margin=(30000, 30000, 30000, 30000),
    )
    for i, month in enumerate(range(1, 13), start=0):
        sid = add_text_shape(
            sp_tree,
            sid,
            f"{month}月",
            left_x + label_w + i * month_w,
            top_y,
            month_w,
            header_h,
            [{"text": f"{month}月", "size": 1200, "bold": True, "color": TEXT, "align": "ctr"}],
            fill=GOLD_LIGHT,
            line=LINE,
            anchor="ctr",
            margin=(20000, 20000, 20000, 20000),
        )

    tasks = [
        ("公司更名及主体信息变更", (1, 4), "完成各项主体信息变更", BLUE),
        ("2025年年审对接", (1, 4), "资料提供、问题回复、调整确认", SAND),
        ("所得税汇算及年度申报", (3, 5), "汇算清缴及调整确认", TEAL),
        ("工商申报", (5, 6), "按节点完成工商申报", ORANGE),
        ("固定资产半年度盘点", (6, 7), "盘点核对并形成结果", GOLD_LIGHT),
        ("半年报披露支持", (6, 9), "整理披露资料并配合输出", CORAL),
    ]

    for idx, (name, (start_m, end_m), bar_text, color) in enumerate(tasks):
        y = top_y + header_h + idx * row_h
        fill = GOLD_PALE if idx % 2 == 0 else WHITE
        sid = add_text_shape(
            sp_tree,
            sid,
            f"task-{idx}",
            left_x,
            y,
            label_w,
            row_h,
            [{"text": name, "size": 1180, "color": TEXT}],
            fill=fill,
            line=LINE,
            margin=(60000, 40000, 40000, 40000),
        )
        for i in range(12):
            sid = add_text_shape(
                sp_tree,
                sid,
                f"grid-{idx}-{i}",
                left_x + label_w + i * month_w,
                y,
                month_w,
                row_h,
                [],
                fill=fill,
                line=LINE,
            )
        bar_x = left_x + label_w + (start_m - 1) * month_w + 15000
        bar_w = (end_m - start_m + 1) * month_w - 30000
        sid = add_text_shape(
            sp_tree,
            sid,
            f"bar-{idx}",
            bar_x,
            y + 45000,
            bar_w,
            row_h - 90000,
            [{"text": bar_text, "size": 1050, "bold": True, "color": TEXT, "align": "ctr"}],
            fill=color,
            line=color,
            round_rect=True,
            anchor="ctr",
            margin=(20000, 20000, 20000, 20000),
        )

    sid = add_card(
        sp_tree,
        sid,
        650000,
        3890000,
        7580000,
        260000,
        "执行提示",
        [
            "月度关账、报表和税务申报作为全年持续工作底座，专项事项按甘特图节点前置准备资料并推动闭环。",
        ],
    )
    return xml_bytes(root), blank_slide_rels()


class PptPackage:
    def __init__(self, template: Path):
        with zipfile.ZipFile(template, "r") as zf:
            self.files = {name: zf.read(name) for name in zf.namelist()}
        self.presentation = parse_xml(self.files["ppt/presentation.xml"])
        self.presentation_rels = parse_xml(self.files["ppt/_rels/presentation.xml.rels"])
        self.content_types = parse_xml(self.files["[Content_Types].xml"])
        self.next_slide_num = max(
            int(m.group(1))
            for name in self.files
            if (m := re.match(r"ppt/slides/slide(\d+)\.xml$", name))
        ) + 1
        self.next_slide_rid = (
            max(
                int(m.group(1))
                for rel in self.presentation_rels.findall(reltag("Relationship"))
                if (m := re.match(r"rId(\d+)", rel.attrib["Id"]))
            )
            + 1
        )
        self.next_sld_id = (
            max(int(s.attrib["id"]) for s in self.presentation.find(ptag("sldIdLst"))) + 1
        )
        self.slide_rel_map = {
            rel.attrib["Id"]: rel.attrib["Target"].replace("slides/", "").replace(".xml", "")
            for rel in self.presentation_rels.findall(reltag("Relationship"))
            if rel.attrib["Type"].endswith("/slide")
        }
        self.sld_id_map = {
            rid: int(s.attrib["id"])
            for s in self.presentation.find(ptag("sldIdLst"))
            for rid in [s.attrib[rtag("id")]]
        }

    def write_file(self, name: str, content: bytes):
        self.files[name] = content

    def slide_root(self, num: int) -> ET.Element:
        return parse_xml(self.files[f"ppt/slides/slide{num}.xml"])

    def save_slide_root(self, num: int, root: ET.Element):
        self.files[f"ppt/slides/slide{num}.xml"] = xml_bytes(root)

    def add_content_type_override(self, slide_num: int):
        part_name = f"/ppt/slides/slide{slide_num}.xml"
        for override in self.content_types.findall(qn(CT_NS, "Override")):
            if override.attrib.get("PartName") == part_name:
                return
        ET.SubElement(
            self.content_types,
            qn(CT_NS, "Override"),
            {
                "PartName": part_name,
                "ContentType": "application/vnd.openxmlformats-officedocument.presentationml.slide+xml",
            },
        )

    def add_custom_slide(self, slide_xml: bytes, rels_xml: bytes) -> tuple[int, str, int]:
        slide_num = self.next_slide_num
        rid = f"rId{self.next_slide_rid}"
        sld_id = self.next_sld_id
        self.next_slide_num += 1
        self.next_slide_rid += 1
        self.next_sld_id += 1
        self.write_file(f"ppt/slides/slide{slide_num}.xml", slide_xml)
        self.write_file(f"ppt/slides/_rels/slide{slide_num}.xml.rels", rels_xml)
        ET.SubElement(
            self.presentation_rels,
            reltag("Relationship"),
            {
                "Id": rid,
                "Type": "http://schemas.openxmlformats.org/officeDocument/2006/relationships/slide",
                "Target": f"slides/slide{slide_num}.xml",
            },
        )
        self.add_content_type_override(slide_num)
        return slide_num, rid, sld_id

    def clone_slide(self, base_num: int) -> tuple[int, str, int]:
        base_root = self.slide_root(base_num)
        for cust in list(base_root.findall(".//p:custDataLst", {"p": P_NS})):
            parent = None
            for elem in base_root.iter():
                if cust in list(elem):
                    parent = elem
                    break
            if parent is not None:
                parent.remove(cust)
        slide_xml = xml_bytes(base_root)
        rels_root = ET.Element(reltag("Relationships"))
        base_rels = parse_xml(self.files[f"ppt/slides/_rels/slide{base_num}.xml.rels"])
        for rel in base_rels.findall(reltag("Relationship")):
            if rel.attrib["Type"].endswith("/slideLayout"):
                ET.SubElement(rels_root, reltag("Relationship"), rel.attrib)
        return self.add_custom_slide(slide_xml, rels_bytes(rels_root))

    def finalize_order(self, order: list[tuple[str, str, int]]):
        sld_lst = self.presentation.find(ptag("sldIdLst"))
        for child in list(sld_lst):
            sld_lst.remove(child)
        for _, rid, sld_id in order:
            ET.SubElement(sld_lst, ptag("sldId"), {"id": str(sld_id), rtag("id"): rid})
        self.files["ppt/presentation.xml"] = xml_bytes(self.presentation)
        self.files["ppt/_rels/presentation.xml.rels"] = rels_bytes(self.presentation_rels)
        self.files["[Content_Types].xml"] = xml_bytes(self.content_types)

    def save(self, path: Path):
        with zipfile.ZipFile(path, "w", zipfile.ZIP_DEFLATED) as zf:
            for name, data in self.files.items():
                zf.writestr(name, data)


def build_slide_content():
    return {
        "summary_1": build_summary_slide(
            "2025年重点工作总结（一）月度结账与报表输出",
            [
                ("核心事项", ["完成费用计提、凭证审核、报表出具与凭证归档。", "同步维护其他应收款台账。"]),
                ("节点输出", ["按月完成单体报表、合并报表和统计申报。", "结账报表构成月度工作主线。"]),
                ("管控动作", ["结账前集中核对物流、工资、报销等关键数据。", "同步复核银行余额和异常科目。"]),
                ("结果价值", ["保障账务处理及时、准确、可追溯。", "支撑月初 5-7 号各项报表输出。"]),
            ],
            "grid",
        ),
        "summary_2": build_process_slide(
            "工作总结",
            "2025年重点工作总结（二）税务申报与税金管理",
            "围绕“核对、计提、申报、反馈”形成税务工作闭环。",
            ("年度主线", ["完成月度、季度税务事项并保持申报口径一致。"]),
            [
                ("进项核对", "共享平台与账面同步复核"),
                ("税金计提", "结账前完成税金测算"),
                ("纳税申报", "按节点完成月度季度申报"),
                ("问题反馈", "税局反馈事项及时闭环"),
            ],
            ("管理价值", ["守住财税合规底线。", "为后续汇算清缴打下基础。"]),
        ),
        "summary_3": build_pillar_slide(
            "工作总结",
            "2025年重点工作总结（三）预算管控、费用审核与凭证质量",
            "通过前置审核、过程监控和结果复核提升凭证质量。",
            [
                ("审核主线", ["开展报销、付款和预算执行审核。", "提前识别超预算事项。", "维持日常审核节奏。"]),
                ("管控抓手", ["重点核对票据真实性。", "关注流程完整与三流一致。", "预算审核是高频工作。"]),
                ("成效体现", ["提升费用入账质量。", "降低调账与流程异常。", "增强预算执行透明度。"]),
            ],
        ),
        "summary_4": build_process_slide(
            "工作总结",
            "2025年重点工作总结（四）三号仓经营核算与利润支撑",
            "围绕“收入确认、成本归集、管报输出、利润预测”形成经营支撑链路。",
            ("业务支撑", ["完成三号仓收入、成本、暂估及管报支撑工作。"]),
            [
                ("收入确认", "按结算及暂估数据入账"),
                ("成本归集", "同步处理费用与预提冲销"),
                ("管报输出", "按节点形成经营管报"),
                ("利润预测", "支撑管理层判断经营趋势"),
            ],
            ("经营价值", ["提升核算精度。", "增强经营分析及时性。"]),
        ),
        "summary_5": build_stakeholder_slide(
            "工作总结",
            "2025年重点工作总结（五）专项事项与外部协同",
            "专项事项涉及主体多、节奏集中，重点在于跨部门协调与节点闭环。",
            "专项协同中心",
            ["年审、更名、盘点、披露", "资料、沟通、反馈三线并行"],
            [
                ("年审对接", ["提供资料并跟进调整事项。", "推动审计问题及时闭环。"]),
                ("主体更名", ["协调税务、银行和系统平台。", "同步处理变更期间衔接事项。"]),
                ("盘点披露", ["完成盘点资料整理。", "配合半年报披露需求。"]),
                ("经验沉淀", ["提前梳理清单和分工。", "跨部门事项统一台账跟踪。"]),
            ],
        ),
        "plan_1": build_pillar_slide(
            "工作规划",
            "2026年工作规划（一）年度规划总目标与重点方向",
            "以“夯实关账质量、守牢税务合规、提升专项协同”为三条主线推进全年工作。",
            [
                ("主线一：核算提质", ["提升结账、报表和台账一致性。", "保证月度输出更稳定。", "缩短月底集中处理压力。"]),
                ("主线二：税务闭环", ["守牢申报、汇算与工商节点。", "提升账税匹配和筹划空间。", "加强共享平台核对支撑。"]),
                ("主线三：协同前置", ["专项事项提前准备资料。", "明确责任与时间节点。", "强化跨部门联动效率。"]),
            ],
        ),
        "plan_3": build_process_slide(
            "工作规划",
            "2026年工作规划（三）月度关账与报表工作规划",
            "围绕“关账节奏清晰、资料归集前置、报表输出准时”三项要求执行。",
            ("规划目标", ["稳定完成结账、报表、统计与归档输出。"]),
            [
                ("月末准备", "完成费用计提与异常排查"),
                ("资料归集", "关键数据实行前置核对"),
                ("报表输出", "月初完成单体与合并报表"),
                ("归档闭环", "同步完成统计和凭证归档"),
            ],
            ("预期结果", ["形成更稳定的月度节奏。", "提升报表与台账一致性。"]),
        ),
        "plan_4": build_compare_slide(
            "工作规划",
            "2026年工作规划（五）税务申报、汇算清缴与工商申报规划",
            "将月度税务、季度税务、年度汇算和工商申报统一纳入节点管理。",
            ("日常税务", ["保持月度、季度申报按期完成。", "加强进项核对和税金计提。"]),
            ("专项节点", ["5 月完成所得税汇算。", "6 月完成工商申报。"]),
            ("预期输出", ["减少临时性被动处理。", "缩短专项事项准备周期。"]),
        ),
        "plan_5": build_pillar_slide(
            "工作规划",
            "2026年工作规划（六）预算审核与凭证质量提升规划",
            "从预算前置、审核标准统一和异常问题复盘三个层面持续优化。",
            [
                ("前置审核", ["统一审核关注点。", "提高事项一次性通过率。", "减少月底返工。"]),
                ("过程监控", ["强化票据、流程和预算检查。", "关注科目归属准确性。", "及时识别异常事项。"]),
                ("复盘改进", ["对高频问题形成复盘。", "推动业务侧同步改进。", "提升凭证准确率。"]),
            ],
        ),
        "plan_6": build_process_slide(
            "工作规划",
            "2026年工作规划（七）三号仓核算与经营分析支撑规划",
            "围绕收入、成本、管报和利润预测构建更稳定的经营支撑节奏。",
            ("规划目标", ["保持收入、成本、管报和预测口径稳定。"]),
            [
                ("数据准备", "前置核对结算与暂估数据"),
                ("入账处理", "同步完成收入成本归集"),
                ("管报输出", "月初形成三号仓管报"),
                ("分析支撑", "配合利润预测与差异分析"),
            ],
            ("预期输出", ["提升核算精度。", "增强经营决策支撑。"]),
        ),
        "plan_7": build_compare_slide(
            "工作规划",
            "2026年工作规划（八）固定资产盘点、半年报披露协同规划",
            "重点抓好盘点前准备、数据核对和资料整理，提升集团协同效率。",
            ("盘点规划", ["提前梳理盘点清单与账实核对步骤。", "按集团要求完成结果输出。"]),
            ("披露协同", ["结合总部通知整理披露资料。", "关键数据复核后提交。"]),
            ("预期输出", ["提高专项准备充分度。", "提升资料一次性通过率。"]),
        ),
        "plan_8": build_stakeholder_slide(
            "工作规划",
            "2026年工作规划（九）专项事项、风险与跨部门协同规划",
            "对更名后续、年审配合和临时监管事项建立更明确的协同机制。",
            "协同治理中心",
            ["专项台账", "风险前置", "责任到人"],
            [
                ("更名后续", ["跟进主体变更收尾事项。", "同步维护系统与流程衔接。"]),
                ("年审配合", ["按清单准备资料。", "及时反馈调整事项。"]),
                ("库存前处理", ["关注闭店后库存前处理。", "避免长期挂账。"]),
                ("临时事项", ["预留应急响应窗口。", "全年动态处理监管反馈。"]),
            ],
        ),
    }


def main():
    deck = PptPackage(TEMPLATE)
    existing_slide_rids = {}
    for rel in deck.presentation_rels.findall(reltag("Relationship")):
        if rel.attrib["Type"].endswith("/slide"):
            match = re.search(r"slide(\d+)\.xml$", rel.attrib["Target"])
            if match:
                existing_slide_rids[int(match.group(1))] = rel.attrib["Id"]

    # Cover
    cover = deck.slide_root(1)
    set_shape_lines(find_shape(cover, "6"), ["2025年重点工作总结及2026年工作规划汇报"], size=2800, bold=True, color=WHITE)
    replace_cover_info(find_shape(cover, "2"))
    deck.save_slide_root(1, cover)

    # Agenda
    agenda = deck.slide_root(2)
    set_shape_lines(find_shape(agenda, "5"), ["个人介绍及岗位职责"], size=1800, bold=False, color=TEXT)
    set_shape_lines(find_shape(agenda, "3"), ["2025年重点工作总结"], size=1800, bold=False, color=TEXT)
    set_shape_lines(find_shape(agenda, "8"), ["2026年工作规划"], size=1800, bold=False, color=TEXT)
    set_shape_lines(find_shape(agenda, "6"), ["个人建议及需协调事项"], size=1800, bold=False, color=TEXT)
    deck.save_slide_root(2, agenda)

    # Section slides
    sec1 = deck.slide_root(3)
    set_chapter_slide(sec1, "一", "个人介绍及岗位职责", ["岗位定位", "核心职责", "工作量概览", "管理价值"])
    deck.save_slide_root(3, sec1)

    sec2 = deck.slide_root(5)
    set_chapter_slide(sec2, "二", "2025年重点工作总结", ["结账报表", "税务管理", "预算审核", "专项协同"])
    deck.save_slide_root(5, sec2)

    sec3_num, sec3_rid, sec3_id = deck.clone_slide(3)
    sec3 = deck.slide_root(sec3_num)
    set_chapter_slide(sec3, "三", "2026年工作规划", ["年度目标", "甘特节点", "税务规划", "风险协同"])
    deck.save_slide_root(sec3_num, sec3)

    sec4_num, sec4_rid, sec4_id = deck.clone_slide(3)
    sec4 = deck.slide_root(sec4_num)
    set_chapter_slide(sec4, "四", "个人建议及需协调事项", ["流程协同", "资料前置", "系统联动", "节点保障"])
    deck.save_slide_root(sec4_num, sec4)

    # Custom slides
    slide_content = build_slide_content()
    overview_num, overview_rid, overview_id = deck.add_custom_slide(*build_overview_slide())
    summary_nums = [deck.add_custom_slide(*slide_content[f"summary_{i}"]) for i in range(1, 6)]
    plan1_num, plan1_rid, plan1_id = deck.add_custom_slide(*slide_content["plan_1"])
    gantt_num, gantt_rid, gantt_id = deck.add_custom_slide(*build_gantt_slide())
    tax_plan_num, tax_plan_rid, tax_plan_id = deck.add_custom_slide(*build_tax_plan_slide())
    plan_other = [deck.add_custom_slide(*slide_content[f"plan_{i}"]) for i in range(3, 9)]
    suggest_num, suggest_rid, suggest_id = deck.add_custom_slide(*build_suggestion_slide())

    order = [
        ("cover", existing_slide_rids[1], deck.sld_id_map[existing_slide_rids[1]]),
        ("agenda", existing_slide_rids[2], deck.sld_id_map[existing_slide_rids[2]]),
        ("sec1", existing_slide_rids[3], deck.sld_id_map[existing_slide_rids[3]]),
        ("overview", overview_rid, overview_id),
        ("sec2", existing_slide_rids[5], deck.sld_id_map[existing_slide_rids[5]]),
    ]
    order.extend((f"summary{i}", rid, sid) for i, (_, rid, sid) in enumerate(summary_nums, start=1))
    order.append(("sec3", sec3_rid, sec3_id))
    order.append(("plan1", plan1_rid, plan1_id))
    order.append(("gantt", gantt_rid, gantt_id))
    order.append(("tax_plan", tax_plan_rid, tax_plan_id))
    order.extend((f"plan{i}", rid, sid) for i, (_, rid, sid) in enumerate(plan_other, start=3))
    order.append(("sec4", sec4_rid, sec4_id))
    order.append(("suggest", suggest_rid, suggest_id))
    order.append(("thanks", existing_slide_rids[7], deck.sld_id_map[existing_slide_rids[7]]))

    deck.finalize_order(order)
    deck.save(OUTPUT)
    print(OUTPUT)


if __name__ == "__main__":
    main()
