import sys
import locale

def recover_file(filename):
    print(f"Recovering {filename}")
    try:
        with open(filename, 'r', encoding='utf-8') as f:
            text = f.read()
    except Exception as e:
        print(f"Read error: {e}")
        return

    if text.startswith('\ufeff'):
        text = text[1:]
        
    encoded_bytes = None
    # Try cp1258
    try:
        encoded_bytes = text.encode('cp1258')
        print(f"Encoded bytes using cp1258 for {filename}")
    except Exception as e:
        print(f"cp1258 error: {e}")
        try:
            encoded_bytes = text.encode('cp1252')
            print(f"Encoded bytes using cp1252 for {filename}")
        except Exception as e:
            print(f"cp1252 error: {e}")

    if encoded_bytes:
        try:
            # Decode using utf-8
            recovered = encoded_bytes.decode('utf-8')
            # Check if there is still mangled text
            if "Ã" in recovered or "Â" in recovered:
                print("Double corrupted? Still has weird chars")
            else:
                with open(filename, 'w', encoding='utf-8') as f:
                    f.write(recovered)
                print(f"Successfully recovered {filename}!")
        except Exception as e:
            print(f"utf-8 decoding error: {e}")

recover_file('tongquan.html')
recover_file('DAT_PHONG.html')
