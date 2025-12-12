# drag-typeracer
Final Project for Object Oriented Programming we created a platform where people could practice their words per minute through interactive drag race

Dari kode sebelumnya, saya mengembangkan beberapa bagian mulai dari menyimpan gambar, menggabungkan UI mengetik dengan arena, dan beberapa hal lain.

- Modularisasi Aset
  Pada pengembangan bagian ini, saya membuat class baru bernama AssetManager. Kelas ini fungsi nya cukup mudah yaitu kelas ini akan menyimpan resource yang akan digunakan ke dalam sebuah memori dengan menggunakan library java. Gambar-gambar ini akan disimpan menggunakan BufferedImage ketika program ini pertama kali di jalankan sehingga mengurangi penggunaan fungs `fillRect`
- Halaman Menu dan Game
  Saya disini melakukan perubahan kecil yaitu membedakan tampilan menu dengan game sehingga pada proyek ini terdapat 2 class panel yaitu GamePanel dan MainMenuPanel. Saya juga mengimplementasikan fungsi `cardLayout` untuk perpindahan halaman antara menu dan game terasa halus. Setelah itu, GamePanel disini juga menjalankan sebuah loop untuk memunculkan sebuah animasi ketika game ini berjalan seperti pergerakan mobil yang halus dan pengetikan yang terlihat real-time.
- 
