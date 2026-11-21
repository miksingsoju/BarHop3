// Split screen like the old no-nav.html: form on one side, drinks photo on the other.
export default function AuthLayout({ children }: LayoutProps<"/">) {
  return (
    <div className="flex min-h-screen flex-col-reverse sm:flex-row">
      <main className="flex flex-1 justify-center bg-gray-200 lg:max-w-[50%] lg:flex-none lg:basis-1/2">
        {children}
      </main>
      <aside
        className="h-40 bg-cover bg-center sm:h-auto sm:flex-1 [background-image:linear-gradient(0deg,rgba(27,27,41,1)_0%,rgba(27,27,41,0.6)_70%,rgba(27,27,41,0.3)_100%),url('/images/drinks.webp')] sm:[background-image:linear-gradient(90deg,rgba(27,27,41,1)_0%,rgba(27,27,41,0.6)_70%,rgba(27,27,41,0.3)_100%),url('/images/drinks.webp')]"
        aria-hidden="true"
      />
    </div>
  );
}
