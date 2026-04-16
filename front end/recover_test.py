import sys
import locale

filename = 'tongquan.html'
try:
    with open(filename, 'r', encoding='utf-8') as f:
        text = f.read()
except Exception as e:
    print(f"Read error: {e}")
    sys.exit(1)

# Try cp1258
try:
    fixed_text_1258 = text.encode('cp1258').decode('utf-8')
    with open('tongquan_1258.html', 'w', encoding='utf-8') as f:
        f.write(fixed_text_1258)
    print("Recovered using cp1258")
except Exception as e:
    print(f"cp1258 error: {e}")

# Try cp1252
try:
    fixed_text_1252 = text.encode('cp1252').decode('utf-8')
    with open('tongquan_1252.html', 'w', encoding='utf-8') as f:
        f.write(fixed_text_1252)
    print("Recovered using cp1252")
except Exception as e:
    print(f"cp1252 error: {e}")

print("System encoding:", locale.getpreferredencoding())

